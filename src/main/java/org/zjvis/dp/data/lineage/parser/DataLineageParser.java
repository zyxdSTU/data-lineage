package org.zjvis.dp.data.lineage.parser;


import java.util.List;
import java.util.Objects;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;
import org.zjvis.dp.data.lineage.data.FieldLineageInfo;
import org.zjvis.dp.data.lineage.data.TableInfo;
import org.zjvis.dp.data.lineage.data.TableLineageInfo;
import org.zjvis.dp.data.lineage.exception.CommonException;
import org.zjvis.dp.data.lineage.parser.ast.CreateTableQuery;
import org.zjvis.dp.data.lineage.parser.ast.DataClause.ClauseType;
import org.zjvis.dp.data.lineage.parser.ast.INode;
import org.zjvis.dp.data.lineage.parser.ast.InsertQuery;
import org.zjvis.dp.data.lineage.parser.ast.SelectStatement;
import org.zjvis.dp.data.lineage.parser.ast.SelectUnionQuery;
import org.zjvis.dp.data.lineage.parser.ast.TableIdentifier;

/**
 * @author zhouyu
 * @create 2023-05-31 15:54
 */
@Component
public class DataLineageParser {
    public static final String LOCAL_SUFFIX = "_local";
    @Resource
    private AstParserFactory astParserFactory;

    public List<FieldLineageInfo> processFieldLineageParse(String sqlType, String sql, String defaultDatabase) {
        InsertQuery insertQuery = getInsertQuery(sqlType, sql);

        if(null == insertQuery) {
            return null;
        }

        FieldLineageDetector fieldLineageDetector = new FieldLineageDetector(defaultDatabase);

        //遍历节点
        fieldLineageDetector.visit(insertQuery);

        return fieldLineageDetector.getFieldLineage();
    }

    public TableLineageInfo processTableLineageParse(String sqlType, String sql, String defaultDatabase) {

        InsertQuery insertQuery = getInsertQuery(sqlType, sql);

        if(null == insertQuery) {
            return null;
        }

        TableLineageDetector tableLineageDetector = new TableLineageDetector(defaultDatabase);

        //遍历节点
        tableLineageDetector.visit(insertQuery);

        return tableLineageDetector.getTableLineage();
    }

    private InsertQuery getInsertQuery(String sqlType, String sql) {
        AstParser astParser = astParserFactory.createAstParser(sqlType);
        INode iNode = astParser.parse(sql);
        if(null == iNode) {
            return null;
        }

        if(!(iNode instanceof InsertQuery)) {
            return null;
        }

        InsertQuery insertQuery = (InsertQuery) iNode;
        if(insertQuery.getDataClause().getClauseType().equals(ClauseType.VALUES)) {
            return null;
        }

        return insertQuery;
    }

    public TableInfo getCreateOrInsertTableInfo(String sqlType, String sql) {
        INode iNode = astParserFactory.createAstParser(sqlType).parse(sql);
        if(iNode instanceof CreateTableQuery) {
            CreateTableQuery createTableQuery = (CreateTableQuery) iNode;
            return visitTableIdentifier(createTableQuery.getIdentifier());
        } else if(iNode instanceof InsertQuery) {
            InsertQuery insertQuery = (InsertQuery) iNode;
            return visitTableIdentifier(insertQuery.getTableIdentifier());
        }
        return null;
    }

    public boolean isSelectQuery(String sqlType, String sql) {
        INode iNode = astParserFactory.createAstParser(sqlType).parse(sql);
        return iNode instanceof SelectUnionQuery;
    }

    public boolean isContainLimit(String sqlType, String sql) {
        INode iNode = astParserFactory.createAstParser(sqlType).parse(sql);
        if(!(iNode instanceof SelectUnionQuery)) {
            return false;
        }
        SelectStatement selectStatement = ((SelectUnionQuery) iNode).getStatements().get(0);
        return Objects.nonNull(selectStatement.getLimitClause()) || Objects.nonNull(selectStatement.getLimitByClause());
    }

    public boolean isInsertValueSQL(String sqlType, String sql) {
        INode iNode = astParserFactory.createAstParser(sqlType).parse(sql);
        if (iNode instanceof InsertQuery) {
            InsertQuery insertQuery = (InsertQuery) iNode;
            if (Objects.nonNull(insertQuery.getDataClause())) {
                return insertQuery.getDataClause().getClauseType() == ClauseType.VALUES;
            }
        }
        return false;
    }

    public void grammarCheck(String sqlType, String sql) {
        //todo 后面优化获取出具体的语法错误
        try {
            astParserFactory.createAstParser(sqlType).parse(sql);
        } catch (Exception e) {
            //todo 后面优化exception
            throw new CommonException("SQL_GRAMMAR_ERROR");
        }
    }

    public TableInfo visitTableIdentifier(TableIdentifier tableIdentifier) {
        if(Objects.isNull(tableIdentifier)) {
            return null;
        }

        //本地表不创建dataSchema
        if(tableIdentifier.getName().endsWith(LOCAL_SUFFIX)) {
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
}
