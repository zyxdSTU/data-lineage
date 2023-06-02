package org.zjvis.dp.data.lineage.parser.ast;

import org.zjvis.dp.data.lineage.parser.AstVisitor;
import org.zjvis.dp.data.lineage.parser.ast.expr.ColumnExpr;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class GroupByClause extends SimpleClause {

    private List<ColumnExpr> groupByExprs;

    public GroupByClause(List<ColumnExpr> groupByExprs) {
        this.groupByExprs = groupByExprs;
    }

    @Override
    public <T> T accept(AstVisitor<T> astVisitor) {
        return astVisitor.visitGroupByClause(this);
    }
}
