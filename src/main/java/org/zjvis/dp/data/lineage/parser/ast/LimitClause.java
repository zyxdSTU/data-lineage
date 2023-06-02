package org.zjvis.dp.data.lineage.parser.ast;

import org.zjvis.dp.data.lineage.parser.AstVisitor;
import lombok.Data;

@Data
public class LimitClause extends INode {

    private boolean withTies;

    private LimitExpr limitExpr;

    public LimitClause(boolean withTies, LimitExpr limitExpr) {
        this.withTies = withTies;
        this.limitExpr = limitExpr;
    }

    @Override
    public <T> T accept(AstVisitor<T> astVisitor) {
        return astVisitor.visitLimitClause(this);
    }

}
