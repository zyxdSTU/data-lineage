package org.zjvis.dp.data.lineage.parser.ast;

import org.zjvis.dp.data.lineage.parser.ast.expr.AssignmentExpr;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class UpdateAlterTableClause extends AlterTableClause {

    private List<AssignmentExpr> list;

    private WhereClause where;

    public UpdateAlterTableClause(List<AssignmentExpr> list, WhereClause where) {
        this.clauseType = ClauseType.UPDATE;
        this.list = list;
        this.where = where;
    }
}
