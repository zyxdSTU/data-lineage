package org.zjvis.dp.data.lineage.parser.ast;

import org.zjvis.dp.data.lineage.parser.ast.expr.TableColumnPropertyType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class RemoveAlterTableClause extends AlterTableClause {

    private boolean ifExists;

    private Identifier identifier;

    private TableColumnPropertyType type;

    public RemoveAlterTableClause(boolean ifExists, Identifier identifier, TableColumnPropertyType type) {
        this.clauseType = ClauseType.REMOVE;
        this.ifExists = ifExists;
        this.identifier = identifier;
        this.type = type;
    }
}
