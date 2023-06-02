package org.zjvis.dp.data.lineage.parser.ast;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class DDLQuery extends Query {

    private StringLiteral clusterName;

}
