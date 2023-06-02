package org.zjvis.dp.data.lineage.parser.ast;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class Query extends INode {

    private String outputFile;

    private String format;

}
