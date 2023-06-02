package org.zjvis.dp.data.lineage.parser.ast;

import org.zjvis.dp.data.lineage.parser.AstVisitor;
import org.zjvis.dp.data.lineage.parser.ast.expr.ColumnExpr;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class PrewhereClause extends SimpleClause {

    private ColumnExpr prewhereExpr;

    public PrewhereClause(ColumnExpr prewhereExpr) {
        this.prewhereExpr = prewhereExpr;
    }

    @Override
    public <T> T accept(AstVisitor<T> astVisitor) {
        return astVisitor.visitPrewhereClause(this);
    }

}
