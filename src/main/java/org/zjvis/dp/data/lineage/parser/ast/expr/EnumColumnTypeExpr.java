package org.zjvis.dp.data.lineage.parser.ast.expr;

import org.zjvis.dp.data.lineage.parser.ast.EnumValue;
import org.zjvis.dp.data.lineage.parser.ast.Identifier;
import java.util.List;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=true)
public class EnumColumnTypeExpr extends ColumnTypeExpr {

    private List<EnumValue> enumValues;

    public EnumColumnTypeExpr(Identifier name, List<EnumValue> enumValues) {
        super(ExprType.ENUM, name);
        this.enumValues = enumValues;
    }

    public List<EnumValue> getEnumValues() {
        return enumValues;
    }

}
