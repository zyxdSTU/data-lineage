package org.zjvis.dp.data.lineage.parser.ast;

import org.zjvis.dp.data.lineage.parser.AstVisitor;
import org.zjvis.dp.data.lineage.parser.ast.expr.OrderExpr;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class OrderByClause extends SimpleClause {

    private List<OrderExpr> orderExprs;

    public OrderByClause(List<OrderExpr> orderExprs) {
        this.orderExprs = orderExprs;
    }

    @Override
    public <T> T accept(AstVisitor<T> astVisitor) {
        return astVisitor.visitOrderByClause(this);
    }
}
