package org.zjvis.dp.data.lineage.parser.ast;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class DropPartitionAlterTableClause extends AlterTableClause {

    private PartitionClause clause;

    public DropPartitionAlterTableClause(PartitionClause clause) {
        this.clauseType = ClauseType.DROP_PARTITION;
        this.clause = clause;
    }
}
