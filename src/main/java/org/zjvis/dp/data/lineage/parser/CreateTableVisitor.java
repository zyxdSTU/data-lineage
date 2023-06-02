package org.zjvis.dp.data.lineage.parser;

import java.util.Objects;
import org.springframework.stereotype.Component;
import org.zjvis.dp.data.lineage.data.TableInfo;
import org.zjvis.dp.data.lineage.parser.ast.CreateTableQuery;
import org.zjvis.dp.data.lineage.parser.ast.TableIdentifier;

/**
 * @author zhouyu
 * @create 2023-03-09 16:52
 */
@Component
public class CreateTableVisitor extends AstVisitor<Object>{

    private TableInfo createTableInfo;


    @Override
    public TableInfo visitTableIdentifier(TableIdentifier tableIdentifier) {
        if(Objects.isNull(tableIdentifier)) {
            return null;
        }
        TableInfo tableInfo = TableInfo.builder()
                .tableName(tableIdentifier.getName())
                .build();
        if(Objects.nonNull(tableIdentifier.getDatabase())) {
            tableInfo.setDatabaseName(tableIdentifier.getDatabase().getName());
        }
        return tableInfo;
    }

    @Override
    public Object visitCreateQuery(CreateTableQuery createTableQuery) {
        if(Objects.nonNull(createTableQuery.getIdentifier())) {
            createTableInfo = visitTableIdentifier(createTableQuery.getIdentifier());
        }
        return super.visitCreateQuery(createTableQuery);
    }

    public TableInfo getCreateTableInfo() {
        return createTableInfo;
    }
}
