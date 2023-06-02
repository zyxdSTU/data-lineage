package org.zjvis.dp.data.lineage.parser.ast.expr;

import org.zjvis.dp.data.lineage.parser.ast.Identifier;
import java.util.List;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=true)
public class NestedColumnTypeExpr extends ColumnTypeExpr {

    private List<ColumnTypeExpr> list;

    public NestedColumnTypeExpr(Identifier name, List<ColumnTypeExpr> list) {
        super(ExprType.NESTED, name);
        this.list = list;
    }

    public List<ColumnTypeExpr> getList() {
        return list;
    }

}
