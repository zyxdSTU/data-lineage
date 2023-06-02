package org.zjvis.dp.data.lineage.parser.ast.expr;

import org.zjvis.dp.data.lineage.parser.ast.INode;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class TableColumnPropertyExpr extends INode {

    private PropertyType propertyType;

    public enum PropertyType
    {
        SIMPLE,
        NAMED,
        COMPLEX,
        ENUM,
        PARAM,
        NESTED,
    }

    private ColumnExpr expr;

    public TableColumnPropertyExpr(PropertyType type, ColumnExpr expr) {
        this.propertyType = type;
        this.expr = expr;
    }

}
