package org.zjvis.dp.data.lineage.parser.ast;

import org.zjvis.dp.data.lineage.parser.ast.expr.ColumnExpr;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class DeleteAlterTableClause extends AlterTableClause {

    private ColumnExpr expr;

    public DeleteAlterTableClause(ColumnExpr expr) {
        this.clauseType = ClauseType.DELETE;
        this.expr = expr;
    }
}
