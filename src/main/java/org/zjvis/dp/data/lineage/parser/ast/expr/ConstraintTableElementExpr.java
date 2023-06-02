package org.zjvis.dp.data.lineage.parser.ast.expr;

import org.zjvis.dp.data.lineage.parser.ast.Identifier;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class ConstraintTableElementExpr extends TableElementExpr {

    private Identifier identifier;

    private ColumnExpr expr;

    public ConstraintTableElementExpr(Identifier identifier, ColumnExpr expr) {
        this.identifier = identifier;
        this.expr = expr;
    }

}
