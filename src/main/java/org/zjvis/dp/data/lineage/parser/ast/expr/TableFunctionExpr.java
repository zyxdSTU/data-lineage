package org.zjvis.dp.data.lineage.parser.ast.expr;

import org.zjvis.dp.data.lineage.parser.AstVisitor;
import org.zjvis.dp.data.lineage.parser.ast.INode;
import org.zjvis.dp.data.lineage.parser.ast.Identifier;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class TableFunctionExpr extends INode {

    private Identifier name;
    
    private List<TableArgExpr> args;

    public TableFunctionExpr(Identifier name, List<TableArgExpr> args) {
        this.name = name;
        this.args = args;
    }

    @Override
    public <T> T accept(AstVisitor<T> astVisitor) {
        return astVisitor.visitTableFunctionExpr(this);
    }
}
