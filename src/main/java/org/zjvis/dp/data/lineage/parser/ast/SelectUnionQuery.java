package org.zjvis.dp.data.lineage.parser.ast;

import org.zjvis.dp.data.lineage.parser.AstVisitor;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class SelectUnionQuery extends Query {

    List<SelectStatement> statements;

    private boolean isScalar;

    public void appendSelect(SelectStatement statement) {
        if (null == statements) {
            statements = new ArrayList<>();
        }
        statements.add(statement);
    }

    public void appendSelect(SelectUnionQuery query) {
        for (SelectStatement statement : query.getStatements()) {
            appendSelect(statement);
        }
    }

    public void shouldBeScalar() {
        isScalar = true;
    }

    @Override
    public <T> T accept(AstVisitor<T> astVisitor) {
        return astVisitor.visitSelectUnionQuery(this);
    }
}
