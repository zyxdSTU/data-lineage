package org.zjvis.dp.data.lineage.parser.ast.expr;

import org.zjvis.dp.data.lineage.parser.ast.Identifier;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=true)
public class SimpleColumnTypeExpr extends ColumnTypeExpr {

    public SimpleColumnTypeExpr(Identifier name) {
        super(ExprType.SIMPLE, name);
    }

}
