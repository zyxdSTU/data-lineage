package org.zjvis.dp.data.lineage.parser.ast;

import org.zjvis.dp.data.lineage.parser.AstVisitor;
import org.zjvis.dp.data.lineage.parser.ast.expr.ColumnExpr;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class ArrayJoinClause extends INode {

    private List<ColumnExpr> exprs;

    private final boolean left;

    public ArrayJoinClause(List<ColumnExpr> exprs, boolean left) {
        this.exprs = exprs;
        this.left = left;
    }

    @Override
    public <T> T accept(AstVisitor<T> astVisitor) {
        return astVisitor.visitArrayJoinClause(this);
    }
}
