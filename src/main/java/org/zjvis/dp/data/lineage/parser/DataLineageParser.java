package org.zjvis.dp.data.lineage.parser;


import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.zjvis.dp.data.lineage.data.DatabaseConfig;
import org.zjvis.dp.data.lineage.data.FieldLineageInfo;
import org.zjvis.dp.data.lineage.data.TableChangeInfo;
import org.zjvis.dp.data.lineage.data.TableInfo;
import org.zjvis.dp.data.lineage.data.TableLineageInfo;
import org.zjvis.dp.data.lineage.enums.SQLType;
import org.zjvis.dp.data.lineage.enums.TableChangeType;
import org.zjvis.dp.data.lineage.exception.DataLineageException;
import org.zjvis.dp.data.lineage.parser.ast.AlterTableQuery;
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

    public List<FieldLineageInfo> processFieldLineageParse(String sqlType, String sql, DatabaseConfig databaseConfig) {
        InsertQuery insertQuery = getInsertQuery(sqlType, sql);

        if(null == insertQuery) {
            return null;
        }

        FieldLineageDetector fieldLineageDetector = new FieldLineageDetector(databaseConfig, sqlType);

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
            throw new DataLineageException("SQL_GRAMMAR_ERROR");
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

    public List<TableInfo> getAllRelateTable(String sql, String defaultDatabase) {
        TableLineageInfo tableLineageInfo = processTableLineageParse(SQLType.CLICKHOUSE.name(), sql, defaultDatabase);
        if(Objects.isNull(tableLineageInfo)) {
            return Lists.newArrayList();
        }
        return tableLineageInfo.getSourceTables();
    }

    public TableChangeInfo getTableChangeInfo(String sqlType, String sql, String defaultDatabase) {
        INode iNode = astParserFactory.createAstParser(sqlType).parse(sql);
        if (iNode instanceof InsertQuery) {
            InsertQuery insertQuery = (InsertQuery) iNode;
            TableIdentifier tableIdentifier = insertQuery.getTableIdentifier();
            String databaseName = defaultDatabase;
            if (Objects.nonNull(tableIdentifier.getDatabase())
                    && StringUtils.isNotBlank(tableIdentifier.getDatabase().getName())) {
                databaseName = tableIdentifier.getDatabase().getName();
            }
            return TableChangeInfo.builder()
                    .tableChangeType(TableChangeType.INSERT.name())
                    .tableName(tableIdentifier.getName())
                    .databaseName(databaseName)
                    .build();
        }

        if (iNode instanceof AlterTableQuery) {
            AlterTableQuery alterTableQuery = (AlterTableQuery) iNode;
            TableIdentifier tableIdentifier = alterTableQuery.getIdentifier();
            String databaseName = defaultDatabase;
            if (Objects.nonNull(tableIdentifier.getDatabase())
                    && StringUtils.isNotBlank(tableIdentifier.getDatabase().getName())) {
                databaseName = tableIdentifier.getDatabase().getName();
            }
            return TableChangeInfo.builder()
                    .tableChangeType(TableChangeType.ALTER.name())
                    .tableName(tableIdentifier.getName())
                    .databaseName(databaseName)
                    .build();
        }

        if (iNode instanceof CreateTableQuery) {
            CreateTableQuery createTableQuery = (CreateTableQuery) iNode;
            TableIdentifier tableIdentifier = createTableQuery.getIdentifier();
            String databaseName = defaultDatabase;
            if (Objects.nonNull(tableIdentifier.getDatabase())
                    && StringUtils.isNotBlank(tableIdentifier.getDatabase().getName())) {
                databaseName = tableIdentifier.getDatabase().getName();
            }
            return TableChangeInfo.builder()
                    .tableChangeType(TableChangeType.CREATE.name())
                    .tableName(tableIdentifier.getName())
                    .databaseName(databaseName)
                    .build();
        }
        return null;
    }

}
