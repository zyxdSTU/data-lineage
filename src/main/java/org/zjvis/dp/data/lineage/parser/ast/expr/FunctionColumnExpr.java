package org.zjvis.dp.data.lineage.parser.ast.expr;

import org.zjvis.dp.data.lineage.parser.ast.Identifier;
import java.util.List;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=true)
public class FunctionColumnExpr extends ColumnExpr {

    private Identifier name;

    private List<ColumnExpr> params;

    private List<ColumnExpr> args;

    protected FunctionColumnExpr(Identifier name, List<ColumnExpr> params, List<ColumnExpr> args) {
        super(ExprType.FUNCTION);
        this.name = name;
        this.params = params;
        this.args = args;
    }

    public Identifier getName() {
        return name;
    }

    public List<ColumnExpr> getParams() {
        return params;
    }

    public List<ColumnExpr> getArgs() {
        return args;
    }

}
