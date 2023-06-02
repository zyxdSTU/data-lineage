package org.zjvis.dp.data.lineage.parser.ast;

import org.zjvis.dp.data.lineage.parser.AstVisitor;
import org.zjvis.dp.data.lineage.parser.ast.expr.ColumnExpr;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class LimitByClause extends INode {

    private LimitExpr limit;

    private List<ColumnExpr> exprs;

    public LimitByClause(LimitExpr limit, List<ColumnExpr> exprs) {
        this.limit = limit;
        this.exprs = exprs;
    }

    @Override
    public <T> T accept(AstVisitor<T> astVisitor) {
        return astVisitor.visitLimitByClause(this);
    }

}
