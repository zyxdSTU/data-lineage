package org.zjvis.dp.data.lineage.parser.ast;

import org.zjvis.dp.data.lineage.parser.AstVisitor;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import org.antlr.v4.runtime.tree.TerminalNode;

@EqualsAndHashCode(callSuper=true)
public class NumberLiteral extends Literal {

    private boolean minus;

    public NumberLiteral(TerminalNode literal) {
        super(LiteralType.NUMBER, literal.getSymbol().getText());
    }

    public NumberLiteral(String literal) {
        super(LiteralType.NUMBER, literal);
    }

    <T> Optional<T> as() {
        // TODO:
        return null;
    }

    public void makeNegative() { minus = true; }

    public boolean isNegative() { return minus; }

    @Override
    public String toString() {
        return (minus ? "-" : "") + super.toString();
    }

    @Override
    public <T> T accept(AstVisitor<T> astVisitor) {
        return astVisitor.visitNumberLiteral(this);
    }
}