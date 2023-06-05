package org.zjvis.dp.data.lineage.parser;

import static org.zjvis.dp.data.lineage.util.DataLineageUtil.distinctByKey;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.zjvis.dp.data.lineage.data.ColumnInfo;
import org.zjvis.dp.data.lineage.data.DatabaseConfig;
import org.zjvis.dp.data.lineage.data.FieldInfo;
import org.zjvis.dp.data.lineage.data.FieldLineageInfo;
import org.zjvis.dp.data.lineage.data.SelectLevelInfo;
import org.zjvis.dp.data.lineage.data.TableInfo;
import org.zjvis.dp.data.lineage.enums.SQLType;
import org.zjvis.dp.data.lineage.exception.DataLineageException;
import org.zjvis.dp.data.lineage.parser.ast.ColumnIdentifier;
import org.zjvis.dp.data.lineage.parser.ast.FromClause;
import org.zjvis.dp.data.lineage.parser.ast.Identifier;
import org.zjvis.dp.data.lineage.parser.ast.InsertQuery;
import org.zjvis.dp.data.lineage.parser.ast.SelectStatement;
import org.zjvis.dp.data.lineage.parser.ast.SelectUnionQuery;
import org.zjvis.dp.data.lineage.parser.ast.TableIdentifier;
import org.zjvis.dp.data.lineage.parser.ast.expr.AliasColumnExpr;
import org.zjvis.dp.data.lineage.parser.ast.expr.AsteriskColumnExpr;
import org.zjvis.dp.data.lineage.parser.ast.expr.ColumnExpr;
import org.zjvis.dp.data.lineage.parser.ast.expr.IdentifierColumnExpr;
import org.zjvis.dp.data.lineage.parser.ast.expr.JoinExpr;
import org.zjvis.dp.data.lineage.parser.ast.expr.TableExpr;
import org.zjvis.dp.data.lineage.parser.database.ClickhouseService;
import org.zjvis.dp.data.lineage.parser.database.DatabaseFactory;
import org.zjvis.dp.data.lineage.parser.database.DatabaseService;
import org.zjvis.dp.data.lineage.util.ApplicationContextGetBeanHelper;
import org.zjvis.dp.data.lineage.util.DataLineageUtil;

/**
 * @author zhouyu
 * @create 2023-02-17 15:30
 */
public class FieldLineageDetector extends AstVisitor<Object> {
    /**
     * 插入的表
     */
    private TableInfo toTableInfo;


    /**
     * 插入的字段
     */
    private List<FieldInfo> toColumnList = Lists.newArrayList();

    /**
     * 当前select查询id
     */
    private String selectId = null;

    /**
     * 解析出的单个字段信息
     */
    private FieldInfo selectFieldInfo = null;

    /**
     * 字段信息临时List
     */
    private List<FieldInfo> selectFieldInfoTempList;

    /**
     * 字段血缘的层次对应关系
     */
    private final Map<String, SelectLevelInfo> selectLevelInfoMap = Maps.newHashMap();

    /**
     * 查询之间的层次关系
     */
    private final Map<String, String> selectLevelParentMap = Maps.newHashMap();

    /**
     * unoin查询之间的主次关系
     */
    private final Map<String, String> unionLevelMap = Maps.newHashMap();


    /**
     * unionMainKey 对应的列
     */
    Map<String, List<FieldInfo>> unionMainKeyFieldMap = Maps.newHashMap();

    /**
     * 是否是别名列、函数列，等相关的列
     */
    boolean isRelateColumn = false;

    private String defaultDatabase;

    private DatabaseConfig databaseConfig;

    private String sqlType;

    FieldLineageDetector(DatabaseConfig databaseConfig, String sqlType) {
        this.defaultDatabase = databaseConfig.getDatabaseName();
        this.databaseConfig = databaseConfig;
        this.sqlType = sqlType;
    }

    private final Cache<String, List<String>> fieldInfoCache = CacheBuilder.newBuilder()
            .initialCapacity(10)
            .maximumSize(50)
            .build();


    public List<String> getFieldFromDatabase(TableInfo tableInfo) {
        DatabaseFactory databaseFactory = ApplicationContextGetBeanHelper.getBean(DatabaseFactory.class);
        DatabaseService databaseService = databaseFactory.createDatabaseService(sqlType);
        List<ColumnInfo> columnInfoList = databaseService.getAllFields(
                databaseConfig,
                tableInfo.getDatabaseName(),
                tableInfo.getTableName()
        );

        if(CollectionUtils.isEmpty(columnInfoList)) {
            return Lists.newArrayList();
        }

        return columnInfoList.stream()
                .map(ColumnInfo::getColumnName)
                .collect(Collectors.toList());
    }

    public String getDefaultDatabaseName() {
        return defaultDatabase;
    }

    public List<String> getFieldFromCache(TableInfo tableInfo) {
        try {
            String databaseName = StringUtils.isEmpty(tableInfo.getDatabaseName()) ? getDefaultDatabaseName() : tableInfo.getDatabaseName();
            tableInfo.setDatabaseName(databaseName);
            String key = String.format("%s.%s", tableInfo.getDatabaseName(), tableInfo.getTableName());
            return fieldInfoCache.get(key, () -> getFieldFromDatabase(tableInfo));
        } catch (ExecutionException e) {
            return Lists.newArrayList();
        }
    }

    public List<FieldInfo> getFieldInfoFromCache(TableInfo tableInfo) {
        List<String> fieldList = getFieldFromCache(tableInfo);
        return fieldList.stream()
                .map(fieldName -> {
                    FieldInfo temp = FieldInfo.builder()
                            .fieldName(fieldName)
                            .tableInfo(tableInfo)
                            .build();
                    temp.setRelatedFieldInfoList(Lists.newArrayList(FieldInfo.copy(temp)));
                    return temp;
                })
                .collect(Collectors.toList());
    }

    @Override
    public String visitIdentifier(Identifier identifier) {
        return identifier.getName();
    }

    @Override
    public TableInfo visitTableIdentifier(TableIdentifier tableIdentifier) {
        if(Objects.isNull(tableIdentifier)) {
            return null;
        }
        TableInfo tableInfo = TableInfo.builder()
                .tableName(tableIdentifier.getName().replace("`", ""))
                .build();
        if(Objects.nonNull(tableIdentifier.getDatabase())) {
            tableInfo.setDatabaseName(tableIdentifier.getDatabase().getName().replace("`", ""));
        }
        return tableInfo;
    }

    @Override
    public Object visitInsertQuery(InsertQuery insertQuery) {
        if (Objects.nonNull(insertQuery.getTableIdentifier())) {
            this.toTableInfo = visitTableIdentifier(insertQuery.getTableIdentifier());
        }

        if (!CollectionUtils.isEmpty(insertQuery.getColumns())) {
            toColumnList.addAll(
                    insertQuery.getColumns().stream()
                            .map(this::visitIdentifier)
                            .map(fieldName -> FieldInfo.builder()
                                        .fieldName(fieldName)
                                        .tableInfo(toTableInfo)
                                        .build()
                            ).collect(Collectors.toList())
            );
        }
        return super.visitInsertQuery(insertQuery);
    }

    @Override
    public Object visitSelectUnionQuery(SelectUnionQuery selectUnionQuery) {
        int index = 0;
        String firstSelectId = null;
        for (SelectStatement selectStatement : selectUnionQuery.getStatements()) {
            index++;
            if(index == 1) {
                firstSelectId = String.valueOf(selectStatement.hashCode());
                unionLevelMap.put(firstSelectId, firstSelectId);
            } else {
                unionLevelMap.put(String.valueOf(selectStatement.hashCode()), firstSelectId);
            }
        }
        return super.visitSelectUnionQuery(selectUnionQuery);
    }



    @Override
    public Object visitSelectStatement(SelectStatement selectStatement) {
        selectId = String.valueOf(selectStatement.hashCode());

        JoinExpr joinExpr = selectStatement.getFromClause().getExpr();

        //多次join
        List<JoinExpr> joinExprList = Lists.newArrayList(joinExpr);
        JoinExpr temp = joinExpr;
        while(Objects.nonNull(temp.getLeftExpr())) {
            joinExprList.add(temp.getRightExpr());
            temp = temp.getLeftExpr();
        }
        if(temp != joinExpr) {
            joinExprList.add(temp);
        }

        joinExprList.forEach(this::disposeJoinExpr);

        return super.visitSelectStatement(selectStatement);
    }

    private void disposeJoinExpr(JoinExpr joinExpr) {
        if(Objects.isNull(joinExpr.getTableExpr())) {
            return;
        }

        TableExpr tableExpr = joinExpr.getTableExpr();
        String tableAlias = Objects.nonNull(tableExpr.getAlias())? tableExpr.getAlias().getName() : null;

        TableInfo tableInfo = null;
        if(Objects.nonNull(tableExpr.getIdentifier())) {
            tableInfo = visitTableIdentifier(tableExpr.getIdentifier());
        }
        if(Objects.nonNull(tableExpr.getExpr()) && Objects.nonNull(tableExpr.getExpr().getIdentifier())) {
            tableInfo = visitTableIdentifier(tableExpr.getExpr().getIdentifier());
        }

        String tableIdentifier = null;
        if(StringUtils.isNotEmpty(tableAlias)) {
            tableIdentifier = tableAlias;
        } else if(Objects.nonNull(tableInfo)) {
            tableIdentifier = tableInfo.getTableName();
        }

        String id = selectId + "_" + (StringUtils.isEmpty(tableIdentifier) ? "" : tableIdentifier);
        SelectLevelInfo selectLevelInfo = SelectLevelInfo.builder()
                .id(id)
                .parentId(selectLevelParentMap.get(selectId))
                .fromTable(tableInfo)
                .tableAlias(tableAlias)
                .build();
        selectLevelInfoMap.put(id, selectLevelInfo);

        //子查询
        List<SelectStatement> selectStatementList = Optional.ofNullable(tableExpr.getSubQuery())
                .map(SelectUnionQuery::getStatements)
                .orElse(Lists.newArrayList());
        selectStatementList.addAll(
                Optional.ofNullable(tableExpr.getExpr())
                        .map(TableExpr::getSubQuery)
                        .map(SelectUnionQuery::getStatements)
                        .orElse(Lists.newArrayList())
        );
        for(SelectStatement element : selectStatementList) {
            selectLevelParentMap.put(String.valueOf(element.hashCode()), id);
        }
    }

    @Override
    public Object visitColumnExprList(List<ColumnExpr> exprs) {
        if(!isRelateColumn) {
            selectFieldInfoTempList = Lists.newArrayList();
        }

        return super.visitColumnExprList(exprs);

    }

    @Override
    public Object visitAsteriskColumnExpr(AsteriskColumnExpr expr) {
        selectFieldInfo.setAsteriskColumn(Boolean.TRUE);
        selectFieldInfo.setTableInfo(visitTableIdentifier(expr.getTable()));
        selectFieldInfo.setFieldName("*");
        return super.visitAsteriskColumnExpr(expr);
    }


    @Override
    public Object visitColumnExpr(ColumnExpr expr) {
        if(isRelateColumn) {
            return super.visitColumnExpr(expr);
        }
        selectFieldInfo = FieldInfo.builder()
                .relatedFieldInfoList(Lists.newArrayList())
                .build();
        Object result = super.visitColumnExpr(expr);
        //relatedField加入本身，是为了后面生成字段级别血缘方便
        if(CollectionUtils.isEmpty(selectFieldInfo.getRelatedFieldInfoList())) {
            selectFieldInfo.getRelatedFieldInfoList().add(FieldInfo.copy(selectFieldInfo));
        }
        selectFieldInfoTempList.add(selectFieldInfo);

        return result;
    }

    @Override
    public Object visitAliasColumnExpr(AliasColumnExpr expr) {
        isRelateColumn = true;
        if(Objects.nonNull(expr.getAlias())) {
            selectFieldInfo.setFieldName(expr.getAlias().getName());
        }
        Object result = super.visitAliasColumnExpr(expr);
        isRelateColumn = false;
        return result;
    }

    public Object visitFunctionColumnExpr(ColumnExpr expr) {
        isRelateColumn = true;
        Object result = super.visitFunctionColumnExpr(expr);
        isRelateColumn = false;
        return result;
    }

    @Override
    public Object visitIdentifierColumnExpr(ColumnExpr expr) {
        if(Objects.isNull(expr) || !(expr instanceof IdentifierColumnExpr)) {
            return super.visitIdentifierColumnExpr(expr);
        }

        IdentifierColumnExpr identifierColumnExpr = (IdentifierColumnExpr) expr;
        ColumnIdentifier columnIdentifier = ((IdentifierColumnExpr) expr).getIdentifier();

        //数字、字符串等字面量跳过
        if(StringUtils.isEmpty(columnIdentifier.getName())
                || StringUtils.isNumericSpace(columnIdentifier.getName())
                || DataLineageUtil.isString(columnIdentifier.getName())) {
            return super.visitIdentifierColumnExpr(expr);
        }


        TableIdentifier tableIdentifier = identifierColumnExpr.getIdentifier().getTable();
        TableInfo tableInfo = null;
        if(Objects.nonNull(tableIdentifier)) {
            tableInfo = TableInfo.builder()
                    .tableName(tableIdentifier.getName())
                    .build();
            if(Objects.nonNull(tableIdentifier.getDatabase())) {
                tableInfo.setDatabaseName(tableIdentifier.getDatabase().getName());
            }
        }



        if(isRelateColumn) {
            selectFieldInfo.getRelatedFieldInfoList().add(
                    FieldInfo.builder()
                            .tableInfo(tableInfo)
                            .fieldName(identifierColumnExpr.getIdentifier().getName())
                            .build()
            ); 
        } else {
            selectFieldInfo.setTableInfo(tableInfo);
            selectFieldInfo.setFieldName(identifierColumnExpr.getIdentifier().getName());
        }

        return super.visitIdentifierColumnExpr(expr);
    }


    /**
     * 字面值列，直接不处理
     * @param expr
     * @return
     */
    @Override
    public Object visitLiteralColumnExpr(ColumnExpr expr) {
        return super.visitLiteralColumnExpr(expr);
    }


    /**
     * 子查询列后面可能用到
     * @param expr
     * @return
     */
    @Override
    public Object visitSubqueryColumnExpr(ColumnExpr expr) {
        return super.visitSubqueryColumnExpr(expr);
    }


    public Object visitFromClause(FromClause fromClause) {
        Map<String, List<FieldInfo>> fieldInfoListMap = Maps.newHashMap();
        int index = 1;
        for(FieldInfo selectFieldInfo : selectFieldInfoTempList) {
            selectFieldInfo.setSequenceNumber(index++);
            //外层select没有别名情况，详见clickhouse.sql情况三
            if (StringUtils.isEmpty(selectFieldInfo.getFieldName())) {
                selectFieldInfo.setFieldName(
                        StringUtils.join(
                                selectFieldInfo.getRelatedFieldInfoList().stream()
                                        .map(FieldInfo::getFieldName)
                                        .collect(Collectors.toList()
                                        ),
                                "_"
                        )
                );
            }
            if(!TableInfo.isNull(selectFieldInfo.getTableInfo())) {
                String key = selectId + "_" + selectFieldInfo.getTableInfo().getTableName();
                fieldInfoListMap.computeIfAbsent(key, t-> Lists.newArrayList());
                fieldInfoListMap.get(key).add(selectFieldInfo);
            } else {
                String key = selectId + "_";
                for(String elementKey : selectLevelInfoMap.keySet()) {
                    if(elementKey.startsWith(key)) {
                        fieldInfoListMap.computeIfAbsent(elementKey, t-> Lists.newArrayList());
                        fieldInfoListMap.get(elementKey).add(selectFieldInfo);
                    }
                }
            }
        }
        fieldInfoListMap.forEach((key, value)->{
            SelectLevelInfo selectLevelInfo = selectLevelInfoMap.get(key);
            selectLevelInfo.setSelectFieldInfoList(value);
        });
        return super.visitFromClause(fromClause);
    }

    public List<FieldLineageInfo> getFieldLineage() {
        replaceAsterisk();

        constructUnionMap();

        List<List<FieldInfo>> targetFieldInfoGroup = getTargetFields();
        if (CollectionUtils.isEmpty(toColumnList)) {
            toColumnList = getFieldFromCache(toTableInfo).stream()
                    .map(fieldName ->
                            FieldInfo.builder()
                                    .fieldName(fieldName)
                                    .tableInfo(toTableInfo)
                                    .build())
                    .collect(Collectors.toList());
        }

        if(CollectionUtils.isEmpty(toColumnList)) {
            throw new DataLineageException("insert statement must specify column list or database information");
        }

        List<FieldLineageInfo> fieldLineageInfoList = Lists.newArrayList();
        for(List<FieldInfo> element : targetFieldInfoGroup) {
            List<Pair<FieldInfo, FieldInfo>> fieldInfoPairList = Lists.newArrayList();
            //重新排序
            resortedTargetFieldList(element);

            //按照sequenceNumber形成map
            Map<Integer, FieldInfo> sequenceFieldMap = element.stream()
                    .collect(Collectors.toMap(FieldInfo::getSequenceNumber, Function.identity()));

            for(Integer sequenceNumber : sequenceFieldMap.keySet()) {
                if(StringUtils.isEmpty(sequenceFieldMap.get(sequenceNumber).getFieldName())) {
                    continue;
                }
                fieldInfoPairList.add(new MutablePair<>(sequenceFieldMap.get(sequenceNumber), toColumnList.get(sequenceNumber -1)));
            }

            fieldLineageInfoList.addAll(fieldInfoPairList.stream()
                    .map(fieldInfoPair -> {
                        List<FieldInfo> sourceFieldInfoList = Lists.newArrayList();
                        getSourceFieldInfo(fieldInfoPair.getLeft(), sourceFieldInfoList, null);
                        //过滤相同的sourceField
                        sourceFieldInfoList = sourceFieldInfoList.stream()
                                .filter(distinctByKey(FieldInfo::getReallyNameWithNumber))
                                .collect(Collectors.toList());
                        return FieldLineageInfo.builder()
                                .targetField(fieldInfoPair.getRight())
                                .sourceFields(sourceFieldInfoList)
                                .build();
                    }).filter(fieldLineageInfo -> CollectionUtils.isNotEmpty(fieldLineageInfo.getSourceFields()))
                    .collect(Collectors.toList())
            );
        }
        if(targetFieldInfoGroup.size() == 1) {
            return fieldLineageInfoList;
        }

        //如果包含union, 需要合并相同的源字段
        Map<String, List<FieldLineageInfo>> map = fieldLineageInfoList.stream()
                .collect(Collectors.groupingBy(fieldLineageInfo -> fieldLineageInfo.getTargetField().getReallyNameWithNumber()));

        List<FieldLineageInfo> result = Lists.newArrayList();

        map.forEach((key, value) -> {
            FieldLineageInfo fieldLineageInfo = FieldLineageInfo.builder()
                    .targetField(value.get(0).getTargetField())
                    .build();
            List<FieldInfo> fieldInfos = value.stream()
                    .map(FieldLineageInfo::getSourceFields)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            fieldLineageInfo.setSourceFields(fieldInfos);
            result.add(fieldLineageInfo);
        });

        return result;
    }


    /**
     * 由于*号的影响，sequenceNumber需要重新排序
     */
    private void resortedTargetFieldList(List<FieldInfo> fieldInfoList) {
        Map<Integer, List<FieldInfo>> sequenceMap = Maps.newHashMap();
        for(FieldInfo fieldInfo : fieldInfoList) {
            sequenceMap.computeIfAbsent(fieldInfo.getSequenceNumber(), t->Lists.newArrayList());
            sequenceMap.get(fieldInfo.getSequenceNumber()).add(fieldInfo);
        }

        int index = 1;
        for(Integer sequenceNumber : sequenceMap.keySet().stream().sorted().collect(Collectors.toList())) {
            for(FieldInfo fieldInfo : sequenceMap.get(sequenceNumber)) {
                fieldInfo.setSequenceNumber(index++);
            }
        }
    }

    /**
     * 在血缘处理前，替换掉*号。example:clickhouse.sql:情况二
     */
    public void replaceAsterisk() {
        for(SelectLevelInfo selectLevelInfoElement : selectLevelInfoMap.values()) {
            if(FieldInfo.isContainAsterisk(selectLevelInfoElement.getSelectFieldInfoList())) {
                getAllFieldInfoWithAsterisk(selectLevelInfoElement);
            }
        }
    }

    public List<FieldInfo> getAllFieldInfoWithAsterisk(SelectLevelInfo selectLevelInfo) {
        if(CollectionUtils.isEmpty(selectLevelInfo.getSelectFieldInfoList())) {
            return selectLevelInfo.getSelectFieldInfoList();
        }

        List<FieldInfo> result = Lists.newArrayList();
        for(FieldInfo fieldInfo : selectLevelInfo.getSelectFieldInfoList()) {
            if(fieldInfo.isAsteriskColumn()) {
                if(!TableInfo.isNull(selectLevelInfo.getFromTable())) {
                    //表名是否一致
                    if(!TableInfo.isNull(fieldInfo.getTableInfo())) {
                        if(!fieldInfo.getTableInfo().equals(selectLevelInfo.getFromTable())) {
                            continue;
                        }
                    }
                    List<FieldInfo> tempList = getFieldInfoFromCache(selectLevelInfo.getFromTable());
                    tempList.forEach(fieldInfoElement -> fieldInfoElement.setSequenceNumber(fieldInfo.getSequenceNumber()));
                    result.addAll(tempList);
                } else {
                    for(SelectLevelInfo selectLevelInfoElement : selectLevelInfoMap.values()) {
                        if(selectLevelInfo.getId().equals(selectLevelInfoElement.getParentId())) {
                            List<FieldInfo> fieldInfos = getAllFieldInfoWithAsterisk(selectLevelInfoElement);
                            if(!TableInfo.isNull(fieldInfo.getTableInfo())) {
                                for(FieldInfo element : fieldInfos) {
                                    element.setTableInfo(fieldInfo.getTableInfo());
                                    if(CollectionUtils.isNotEmpty(element.getRelatedFieldInfoList())
                                            && element.getRelatedFieldInfoList().size() == 1) {
                                        element.getRelatedFieldInfoList().get(0).setTableInfo(fieldInfo.getTableInfo());
                                    }
                                }
                            }
                            result.addAll(getAllFieldInfoWithAsterisk(selectLevelInfoElement));
                        }
                    }
                }
            } else {
                result.add(fieldInfo);
            }
        }
        selectLevelInfo.setSelectFieldInfoList(result);
        return result;
    }


    public void getSourceFieldInfo(FieldInfo targetFieldInfo, List<FieldInfo> sourceFieldInfoList, String parentId) {
        for(SelectLevelInfo selectLevelInfo : selectLevelInfoMap.values()) {
            //找同级
            if(StringUtils.isNotEmpty(selectLevelInfo.getParentId()) && !StringUtils.equals(selectLevelInfo.getParentId(), parentId)) {
                continue;
            }
            if(StringUtils.isEmpty(selectLevelInfo.getParentId()) && StringUtils.isNotEmpty(parentId)) {
                continue;
            }

            if(CollectionUtils.isEmpty(selectLevelInfo.getSelectFieldInfoList())) {
                continue;
            }

            for (FieldInfo element : selectLevelInfo.getSelectFieldInfoList()) {
                //字段名和表名需要符合
                if (!element.getFieldName().equals(targetFieldInfo.getFieldName())) {
                    //union情况下，unionMain字段匹配也可
                    String key = selectLevelInfo.getId().split("_")[0];
                    if(unionLevelMap.get(key).equals(key)) {
                        continue;
                    }

                    FieldInfo unionFieldInfo = unionMainKeyFieldMap.get(unionLevelMap.get(key))
                            .stream()
                            .filter(fieldInfo -> fieldInfo.getSequenceNumber().equals(element.getSequenceNumber()))
                            .collect(Collectors.toList())
                            .get(0);

                    if(!unionFieldInfo.getFieldName().equals(targetFieldInfo.getFieldName())) {
                        continue;
                    }

                }

                if (TableInfo.isNull(selectLevelInfo.getFromTable())) {
                    for (FieldInfo subElement : element.getRelatedFieldInfoList()) {
                        //详见clickhouse.sql情况一，当初加进来的错误
                        if(!TableInfo.isNull(subElement.getTableInfo()) && StringUtils.isNotEmpty(selectLevelInfo.getTableAlias())) {
                            if(!subElement.getTableInfo().getTableName().equals(selectLevelInfo.getTableAlias())) {
                                continue;
                            }
                        }
                        getSourceFieldInfo(subElement, sourceFieldInfoList, selectLevelInfo.getId());
                    }

                } else {
                    List<String> fieldList = getFieldFromCache(selectLevelInfo.getFromTable());
                    for (FieldInfo subElement : element.getRelatedFieldInfoList()) {
                            if(CollectionUtils.isNotEmpty(fieldList) && !fieldList.contains(subElement.getFieldName())) {
                                continue;
                            }
                            sourceFieldInfoList.add(FieldInfo.builder()
                                    .fieldName(subElement.getFieldName())
                                    .tableInfo(selectLevelInfo.getFromTable())
                                    .build()
                            );
                    }
                }
            }
        }
    }

    /**
     * 获取目标字段，即insert列或者最外层select, 需要进行相应排序
     * 由于union的影响，可能有多组targetField
     * @return
     */
    public List<List<FieldInfo>> getTargetFields() {
        Map<String, List<SelectLevelInfo>> map = selectLevelInfoMap.values().stream()
                .filter(selectLevelInfo -> StringUtils.isEmpty(selectLevelInfo.getParentId()))
                .collect(Collectors.groupingBy(selectLevelInfo -> selectLevelInfo.getId().split("_")[0]));

        List<List<FieldInfo>> result = Lists.newArrayList();

        map.forEach((key, value) -> {
            List<FieldInfo> fieldInfos = value.stream()
                    .map(SelectLevelInfo::getSelectFieldInfoList)
                    .flatMap(Collection::stream)
                    .filter(distinctByKey(FieldInfo::getReallyNameWithNumber))
                    .sorted(Comparator.comparing(FieldInfo::getSequenceNumber))
                    .collect(Collectors.toList());
            result.add(fieldInfos);
        });

        return result;
    }

    private void constructUnionMap() {
        List<String> unionMainKey = unionLevelMap.entrySet()
                .stream()
                .filter(element -> element.getKey().equals(element.getValue()))
                .map(Entry::getKey)
                .collect(Collectors.toList());

        for(SelectLevelInfo selectLevelInfo : selectLevelInfoMap.values()) {
            String key = selectLevelInfo.getId().split("_")[0];
            if(unionMainKey.contains(key)) {
                unionMainKeyFieldMap.computeIfAbsent(key, t ->Lists.newArrayList());
                unionMainKeyFieldMap.get(key).addAll(selectLevelInfo.getSelectFieldInfoList());
            }
        }
    }
}
