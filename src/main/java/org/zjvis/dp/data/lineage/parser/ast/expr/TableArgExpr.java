package org.zjvis.dp.data.lineage.parser.ast.expr;

import org.zjvis.dp.data.lineage.parser.AstVisitor;
import org.zjvis.dp.data.lineage.parser.ast.INode;
import org.zjvis.dp.data.lineage.parser.ast.Literal;
import org.zjvis.dp.data.lineage.parser.ast.TableIdentifier;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class TableArgExpr extends INode {

    private Literal literal;

    private TableFunctionExpr functionExpr;

    private TableIdentifier identifier;

    public TableArgExpr(Literal literal) {
        this.literal = literal;
    }

    public TableArgExpr(TableFunctionExpr functionExpr) {
        this.functionExpr = functionExpr;
    }

    public TableArgExpr(TableIdentifier identifier) {
        this.identifier = identifier;
    }

    @Override
    public <T> T accept(AstVisitor<T> astVisitor) {
        return astVisitor.visitTableArgExpr(this);
    }
}
