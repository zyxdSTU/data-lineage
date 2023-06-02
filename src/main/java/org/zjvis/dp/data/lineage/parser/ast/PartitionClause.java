package org.zjvis.dp.data.lineage.parser.ast;

import org.zjvis.dp.data.lineage.parser.AstVisitor;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class PartitionClause extends INode {

    public enum ClauseType {
        ID,
        LIST
    }

    private final ClauseType clauseType;

    private Literal id;

    private List<Literal> list;

    public PartitionClause(ClauseType clauseType) {
        this.clauseType = clauseType;
    }

    public PartitionClause(Literal id) {
        this.clauseType = ClauseType.ID;
        this.id = id;
    }

    public PartitionClause(List<Literal> list) {
        this.clauseType = ClauseType.LIST;
        this.list = list;
    }

    @Override
    public <T> T accept(AstVisitor<T> astVisitor) {
        return astVisitor.visitPartitionClause(this);
    }
}
