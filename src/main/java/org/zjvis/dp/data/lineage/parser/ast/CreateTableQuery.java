package org.zjvis.dp.data.lineage.parser.ast;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.zjvis.dp.data.lineage.parser.AstVisitor;

@Data
@EqualsAndHashCode(callSuper=true)
public class CreateTableQuery extends DDLQuery {

    private boolean attach;

    private boolean temporary;

    private boolean ifNotExists;

    private TableIdentifier identifier;

    private StringLiteral uuidClause;

    private TableSchemaClause schema;

    private EngineClause engine;

    private SelectUnionQuery query;

    public CreateTableQuery(
            StringLiteral cluster,
            boolean attach,
            boolean temporary,
            boolean ifNotExists,
            TableIdentifier identifier,
            StringLiteral uuidClause,
            TableSchemaClause schema,
            EngineClause engine,
            SelectUnionQuery query) {
        setClusterName(cluster);
        this.attach = attach;
        this.temporary = temporary;
        this.ifNotExists = ifNotExists;
        this.identifier = identifier;
        this.uuidClause = uuidClause;
        this.schema = schema;
        this.engine = engine;
        this.query = query;
    }

    @Override
    public <T> T accept(AstVisitor<T> astVisitor) {
        return astVisitor.visitCreateQuery(this);
    }
}
