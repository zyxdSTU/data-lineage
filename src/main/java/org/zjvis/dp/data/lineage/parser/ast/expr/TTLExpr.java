package org.zjvis.dp.data.lineage.parser.ast.expr;

import org.zjvis.dp.data.lineage.parser.AstVisitor;
import org.zjvis.dp.data.lineage.parser.ast.INode;
import org.zjvis.dp.data.lineage.parser.ast.StringLiteral;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class TTLExpr extends INode {

    private ColumnExpr expr;

    private StringLiteral literal;

    private TTLType ttlType;

    public enum TTLType {
        DELETE,
        TO_DISK,
        TO_VOLUME
    }

    @Override
    public <T> T accept(AstVisitor<T> astVisitor) {
        return astVisitor.visitTTLExpr(this);
    }
}
