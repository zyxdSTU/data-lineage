package org.zjvis.dp.data.lineage.parser.ast.expr;

import org.zjvis.dp.data.lineage.parser.ast.SelectUnionQuery;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=true)
public class SubqueryColumnExpr extends ColumnExpr {

    private SelectUnionQuery query;

    protected SubqueryColumnExpr(SelectUnionQuery query) {
        super(ExprType.SUBQUERY);
        this.query = query;
    }

    public SelectUnionQuery getQuery() {
        return query;
    }

}
