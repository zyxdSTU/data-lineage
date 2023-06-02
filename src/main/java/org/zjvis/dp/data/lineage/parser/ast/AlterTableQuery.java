package org.zjvis.dp.data.lineage.parser.ast;

import org.zjvis.dp.data.lineage.parser.AstVisitor;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AlterTableQuery extends Query {

    private StringLiteral cluster;

    private TableIdentifier identifier;

    private List<AlterTableClause> clauses;

    public AlterTableQuery(StringLiteral cluster, TableIdentifier identifier, List<AlterTableClause> clauses) {
        this.cluster = cluster;
        this.identifier = identifier;
        this.clauses = clauses;
    }

    @Override
    public <T> T accept(AstVisitor<T> astVisitor) {
        return astVisitor.visitAlterTableQuery(this);
    }
}
