package org.zjvis.dp.data.lineage.parser.ast.expr;

import org.zjvis.dp.data.lineage.parser.AstVisitor;
import org.zjvis.dp.data.lineage.parser.ast.INode;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class CodecExpr extends INode {

    private List<CodecArgExpr> codeArgExprList;

    public CodecExpr(List<CodecArgExpr> codeArgExprList) {
        this.codeArgExprList = codeArgExprList;
    }

    @Override
    public <T> T accept(AstVisitor<T> astVisitor) {
        return astVisitor.visitCodecExpr(this);
    }
}
