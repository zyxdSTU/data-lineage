package org.zjvis.dp.data.lineage.parser.ast;

import org.zjvis.dp.data.lineage.parser.AstVisitor;
import org.zjvis.dp.data.lineage.parser.ast.expr.TableElementExpr;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class AddColumnAlterTableClause extends AlterTableClause {

    private boolean ifNotExists;

    private TableElementExpr element;

    private Identifier after;

    public AddColumnAlterTableClause(boolean ifNotExists, TableElementExpr element, Identifier after) {
        this.clauseType = ClauseType.ADD_COLUMN;
        this.ifNotExists = ifNotExists;
        this.element = element;
        this.after = after;
    }

    @Override
    public <T> T accept(AstVisitor<T> astVisitor) {
        return astVisitor.visitAddColumnAlterTableClause(this);
    }
}
