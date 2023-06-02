package org.zjvis.dp.data.lineage.parser.ast.expr;

import org.zjvis.dp.data.lineage.parser.AstVisitor;
import org.zjvis.dp.data.lineage.parser.ast.INode;
import org.zjvis.dp.data.lineage.parser.ast.Identifier;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class CodecArgExpr extends INode {

    private Identifier identifier;
    List<ColumnExpr> list;

    public CodecArgExpr(Identifier identifier, List<ColumnExpr> list) {
        this.identifier = identifier;
        this.list = list;
    }

    @Override
    public <T> T accept(AstVisitor<T> astVisitor) {
        return astVisitor.visitCodecArgExpr(this);
    }
}
