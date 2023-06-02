package org.zjvis.dp.data.lineage.parser;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import org.zjvis.dp.data.lineage.data.TableInfo;
import org.zjvis.dp.data.lineage.data.TableLineageInfo;
import org.zjvis.dp.data.lineage.parser.ast.InsertQuery;
import org.zjvis.dp.data.lineage.parser.ast.TableIdentifier;
import org.zjvis.dp.data.lineage.parser.ast.expr.JoinExpr;
import org.zjvis.dp.data.lineage.parser.ast.expr.TableExpr;

/**
 * @author zhouyu
 * @create 2023-03-27 14:48
 */
public class TableLineageDetector extends AstVisitor<Object>{

    /**
     * 所有select表
     */
    List<TableInfo> allSelectTableList = Lists.newArrayList();

    /**
     * 插入的表
     */
    private TableInfo toTableInfo;


    private String defaultDatabase;

    TableLineageDetector(String defaultDatabase) {
        this.defaultDatabase = defaultDatabase;
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
        return super.visitInsertQuery(insertQuery);
    }


    @Override
    public Object visitJoinExpr(JoinExpr joinExpr) {
        TableExpr tableExpr = joinExpr.getTableExpr();

        while(Objects.nonNull(tableExpr)) {
            if (Objects.nonNull(tableExpr.getIdentifier())) {
                allSelectTableList.add(visitTableIdentifier(tableExpr.getIdentifier()));
            }
            tableExpr = tableExpr.getExpr();
        }
        return super.visitJoinExpr(joinExpr);
    }

    public TableLineageInfo getTableLineage() {
        return TableLineageInfo.builder()
                .targetTable(toTableInfo)
                .sourceTables(allSelectTableList)
                .build();
    }
}
