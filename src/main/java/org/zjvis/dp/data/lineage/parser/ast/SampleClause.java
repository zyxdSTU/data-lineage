package org.zjvis.dp.data.lineage.parser.ast;

import org.zjvis.dp.data.lineage.parser.AstVisitor;
import org.zjvis.dp.data.lineage.parser.ast.expr.RatioExpr;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class SampleClause extends INode {

    private RatioExpr ratio;

    private RatioExpr offset;

    public SampleClause(RatioExpr ratio, RatioExpr offset) {
        this.ratio = ratio;
        this.offset = offset;
    }

    @Override
    public <T> T accept(AstVisitor<T> astVisitor) {
        return astVisitor.visitSampleClause(this);
    }
}
