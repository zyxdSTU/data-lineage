package org.zjvis.dp.data.lineage.parser.ast;


import org.zjvis.dp.data.lineage.parser.AstVisitor;
import org.zjvis.dp.data.lineage.parser.ast.expr.TTLExpr;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class TTLClause extends SimpleClause {

    private List<TTLExpr> ttlExprList;

    @Override
    public <T> T accept(AstVisitor<T> astVisitor) {
        return astVisitor.visitTTLClause(this);
    }
}
