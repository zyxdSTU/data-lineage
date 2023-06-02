package org.zjvis.dp.data.lineage.parser.ast;

import org.zjvis.dp.data.lineage.parser.ast.expr.CodecExpr;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class CodecAlterTableClause extends AlterTableClause {

    private boolean ifExists;

    private Identifier identifier;

    private CodecExpr codec;

    public CodecAlterTableClause(boolean ifExists, Identifier identifier, CodecExpr codec) {
        this.clauseType = ClauseType.CODEC;
        this.ifExists = ifExists;
        this.identifier = identifier;
        this.codec = codec;
    }
}
