package org.zjvis.dp.data.lineage.parser.ast;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=true)
public class DatabaseIdentifier extends Identifier {

    public DatabaseIdentifier(Identifier name) {
        super(null != name ? name.getName() : "");
    }

}
