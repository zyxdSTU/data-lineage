package org.zjvis.dp.data.lineage.parser.ast;

import org.zjvis.dp.data.lineage.parser.AstVisitor;
import org.zjvis.dp.data.lineage.parser.ast.expr.ColumnExpr;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class JoinConstraintClause extends SimpleClause {

    private ConstraintType type;

    private List<ColumnExpr> exprs;

    public enum ConstraintType
    {
        ON,
        USING,
    }

    public JoinConstraintClause(ConstraintType type, List<ColumnExpr> exprs) {
        this.type = type;
        this.exprs = exprs;
    }

    @Override
    public <T> T accept(AstVisitor<T> astVisitor) {
        return astVisitor.visitJoinConstraintClause(this);
    }
}
