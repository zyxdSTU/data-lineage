package org.zjvis.dp.data.lineage.mysql;

import java.util.stream.Collectors;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.collections.CollectionUtils;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.ConstantContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.DottedIdContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.FullColumnNameContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.FullColumnNameListContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.FullIdContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.HexadecimalLiteralContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.InnerJoinContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.NaturalJoinContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.OuterJoinContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.SimpleIdContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.StraightJoinContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.StringLiteralContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.TableNameContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.UidContext;
import org.zjvis.dp.data.lineage.parser.ast.ColumnIdentifier;
import org.zjvis.dp.data.lineage.parser.ast.Identifier;
import org.zjvis.dp.data.lineage.parser.ast.TableIdentifier;

/**
 * @author zhouyu
 * @create 2023-05-19 17:13
 */
public class MySqlVisitor extends MySqlParserBaseVisitor {

    @Override
    public Object visitFullColumnNameList(FullColumnNameListContext ctx) {
        return ctx.fullColumnName().stream()
                .map(this::visit)
                .collect(Collectors.toList());
    }

    @Override
    public Object visitFullColumnName(FullColumnNameContext ctx) {
        ColumnIdentifier columnIdentifier = null;
        TableIdentifier tableIdentifier = null;
        if (null != ctx.uid()) {
            Identifier firstIdentifier = (Identifier) visit(ctx.uid());
            Identifier secondIdentifier;
            Identifier thirdIdentifier;
            if (CollectionUtils.isNotEmpty(ctx.dottedId())) {
                secondIdentifier = (Identifier) visit(ctx.dottedId(0));
                if (ctx.dottedId().size() == 1) {
                    tableIdentifier = new TableIdentifier(null, firstIdentifier);
                    columnIdentifier = new ColumnIdentifier(tableIdentifier, secondIdentifier);
                } else {
                    thirdIdentifier = (Identifier) visit(ctx.dottedId(1));
                    tableIdentifier = new TableIdentifier(firstIdentifier, secondIdentifier);
                    columnIdentifier = new ColumnIdentifier(tableIdentifier, thirdIdentifier);
                }
            } else {
                columnIdentifier = new ColumnIdentifier(null, firstIdentifier);
            }
        }
        //未处理 .? dottedId dottedId?情况
        return columnIdentifier;
    }


    @Override
    public Object visitInnerJoin(InnerJoinContext ctx) {
        return visit(ctx.tableSourceItem());
    }


    @Override
    public Object visitStraightJoin(StraightJoinContext ctx) {
        return visit(ctx.tableSourceItem());
    }


    @Override
    public Object visitOuterJoin(OuterJoinContext ctx) {
        return visit(ctx.tableSourceItem());
    }


    @Override
    public Object visitNaturalJoin(NaturalJoinContext ctx) {
        return super.visitNaturalJoin(ctx);
    }


    @Override
    public Object visitStringLiteral(StringLiteralContext ctx) {
        //todo 默认返回第一个，后面修改
        return visit(ctx.STRING_LITERAL(0));
    }


    @Override
    public Object visitHexadecimalLiteral(HexadecimalLiteralContext ctx) {
        if (null != ctx.STRING_CHARSET_NAME()) {
            Identifier charSetIdentifier = (Identifier) visit(ctx.STRING_CHARSET_NAME());
            Identifier hexIdentifier = (Identifier) visit(ctx.HEXADECIMAL_LITERAL());
            return new Identifier(charSetIdentifier.getName() + " " + hexIdentifier.getName());
        } else {
            return visit(ctx.HEXADECIMAL_LITERAL());
        }
    }


    @Override
    public Object visitTableName(TableNameContext ctx) {
        Identifier identifier = (Identifier) visit(ctx.fullId());
        TableIdentifier tableIdentifier;
        String[] strArr = identifier.getName().split("\\.");
        if (strArr.length == 2) {
            tableIdentifier = new TableIdentifier(new Identifier(strArr[0]), new Identifier(strArr[1]));
        } else {
            tableIdentifier = new TableIdentifier(null, new Identifier(strArr[0]));
        }
        return tableIdentifier;
    }


    @Override
    public Object visitFullId(FullIdContext ctx) {
        StringBuilder result = new StringBuilder();
        if (CollectionUtils.isNotEmpty(ctx.uid())) {
            if (ctx.uid().size() == 2) {
                String uidFirst = ((Identifier) visit(ctx.uid(0))).getName();
                String uidSecond = ((Identifier) visit(ctx.uid(1))).getName();
                result.append(uidFirst).append(".").append(uidSecond);
            } else {
                String uidFirst = ((Identifier) visit(ctx.uid(0))).getName();
                result.append(uidFirst);
            }
        }
        if (null != ctx.DOT_ID()) {
            result.append((String) visit(ctx.DOT_ID()));
        }
        return new Identifier(result.toString());
    }


    @Override
    public Object visitUid(UidContext ctx) {
        if (null != ctx.simpleId()) {
            return visit(ctx.simpleId());
        }
        return null;
    }


    @Override
    public Object visitSimpleId(SimpleIdContext ctx) {
        if (null != ctx.ID()) {
            return visit(ctx.ID());
        } else {
            //字段为关键词会触发
            return new Identifier(ctx.getText());
        }
    }


    @Override
    public Object visitDottedId(DottedIdContext ctx) {
        if (null != ctx.DOT_ID()) {
            Identifier identifier = (Identifier) visit(ctx.DOT_ID());
            return new Identifier(identifier.getName().substring(1));
        }
        return null;
    }


    @Override
    public Object visitTerminal(TerminalNode node) {
        return new Identifier(node.getSymbol().getText());
    }


    @Override
    public Object visitConstant(ConstantContext ctx) {
        if (null != ctx.stringLiteral()) {
            return visit(ctx.stringLiteral());
        }

        if (null != ctx.decimalLiteral()) {
            return visit(ctx.decimalLiteral());
        }

        if (null != ctx.hexadecimalLiteral()) {
            return visit(ctx.hexadecimalLiteral());
        }

        if (null != ctx.booleanLiteral()) {
            return visit(ctx.booleanLiteral());
        }

        if (null != ctx.REAL_LITERAL()) {
            return visit(ctx.REAL_LITERAL());
        }

        if (null != ctx.BIT_STRING()) {
            return visit(ctx.BIT_STRING());
        }

        if (null != ctx.NOT()) {
            Identifier notIdentifier = (Identifier) visit(ctx.NOT());

            String nullStr = ctx.nullLiteral.getText();

            return new Identifier(notIdentifier.getName() + " " + nullStr);
        }

        if (null != ctx.nullLiteral) {
            return new Identifier(ctx.nullLiteral.getText());
        }

        return null;
    }
}
