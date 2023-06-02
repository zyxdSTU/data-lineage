package org.zjvis.dp.data.lineage.clickhouse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.springframework.stereotype.Component;
import org.zjvis.dp.data.lineage.enums.SQLType;
import org.zjvis.dp.data.lineage.parser.AstParser;
import org.zjvis.dp.data.lineage.parser.CstVisitor;
import org.zjvis.dp.data.lineage.parser.ast.INode;

/**
 * @author zhouyu
 * @create 2023-05-31 15:10
 */
@Component
public class ClickhouseAstParser implements AstParser {

    @Override
    public String getSQLType() {
        return SQLType.CLICKHOUSE.name();
    }

    @Override
    public INode parse(String sql) {
        try {
            // try parsing a SQL
            InputStream inputStream = new ByteArrayInputStream(sql.getBytes());
            ClickHouseLexer ckLexer = new ClickHouseLexer(CharStreams.fromStream(inputStream));
            TokenStream tokens = new CommonTokenStream(ckLexer);
            ClickHouseParser ckParser = new ClickHouseParser(tokens);
            ClickHouseParser.QueryStmtContext tree = ckParser.queryStmt();
            // Notice: ckParser.queryStmt() can only be called once as it reads data from stream
            Object result = new CstVisitor().visit(tree);

            if(null == result) {
                return null;
            } else {
                return (INode) result;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
