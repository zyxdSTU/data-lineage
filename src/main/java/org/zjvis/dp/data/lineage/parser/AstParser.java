package org.zjvis.dp.data.lineage.parser;

import org.zjvis.dp.data.lineage.parser.ast.INode;

/**
 * @author zhouyu
 * @create 2023-05-31 15:06
 */
public interface AstParser {
    String getSQLType();


    INode parse(String sql);
}
