package org.zjvis.dp.data.lineage.parser.ast;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class EnumValue extends INode {

    private StringLiteral name;

    private NumberLiteral value;

    public EnumValue(StringLiteral name, NumberLiteral value) {
        this.name = name;
        this.value = value;
    }

}