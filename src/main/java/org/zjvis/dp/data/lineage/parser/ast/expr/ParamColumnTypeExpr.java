package org.zjvis.dp.data.lineage.parser.ast.expr;

import org.zjvis.dp.data.lineage.parser.ast.Identifier;
import java.util.List;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=true)
public class ParamColumnTypeExpr extends ColumnTypeExpr {

    private List<ColumnExpr> params;

    public ParamColumnTypeExpr(Identifier name, List<ColumnExpr> params) {
        super(ExprType.PARAM, name);
        this.params = params;
    }

    public List<ColumnExpr> getParams() {
        return params;
    }

}
