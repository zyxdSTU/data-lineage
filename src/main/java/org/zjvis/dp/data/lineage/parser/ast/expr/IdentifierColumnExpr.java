package org.zjvis.dp.data.lineage.parser.ast.expr;

import org.zjvis.dp.data.lineage.parser.ast.ColumnIdentifier;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=true)
public class IdentifierColumnExpr extends ColumnExpr {

    private ColumnIdentifier identifier;

    protected IdentifierColumnExpr(ColumnIdentifier identifier) {
        super(ExprType.IDENTIFIER);
        this.identifier = identifier;
    }

    public ColumnIdentifier getIdentifier() {
        return identifier;
    }
}
