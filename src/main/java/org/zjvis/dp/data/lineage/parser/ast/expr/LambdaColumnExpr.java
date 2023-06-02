package org.zjvis.dp.data.lineage.parser.ast.expr;

import org.zjvis.dp.data.lineage.parser.ast.Identifier;
import java.util.List;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=true)
public class LambdaColumnExpr extends ColumnExpr {

    private List<Identifier> lambdaArgs;

    private ColumnExpr lambdaExpr;

    protected LambdaColumnExpr(List<Identifier> lambdaArgs, ColumnExpr lambdaExpr) {
        super(ExprType.LAMBDA);
        this.lambdaArgs = lambdaArgs;
        this.lambdaExpr = lambdaExpr;
    }

    public List<Identifier> getLambdaArgs() {
        return lambdaArgs;
    }

    public ColumnExpr getLambdaExpr() {
        return lambdaExpr;
    }
}
