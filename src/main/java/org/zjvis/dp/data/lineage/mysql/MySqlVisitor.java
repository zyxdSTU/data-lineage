package org.zjvis.dp.data.lineage.mysql;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.collections.CollectionUtils;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.AtomTableItemContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.BitExpressionAtomContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.BooleanLiteralContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.ConstantContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.DecimalLiteralContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.DottedIdContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.ExpressionAtomPredicateContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.ExtractFunctionCallContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.FromClauseContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.FullColumnNameContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.FullColumnNameExpressionAtomContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.FullColumnNameListContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.FullIdContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.FunctionArgsContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.FunctionNameBaseContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.HexadecimalLiteralContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.InnerJoinContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.InsertStatementContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.InsertStatementValueContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.IntervalTypeBaseContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.IntervalTypeContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.JoinPartContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.JsonExpressionAtomContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.LogicalExpressionContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.MathExpressionAtomContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.NaturalJoinContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.NotExpressionContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.OuterJoinContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.ParenthesisSelectContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.PredicateExpressionContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.QueryExpressionContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.QueryExpressionNointoContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.QuerySpecificationContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.QuerySpecificationNointoContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.RootContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.ScalarFunctionCallContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.ScalarFunctionNameContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.SelectColumnElementContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.SelectElementsContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.SelectExpressionElementContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.SelectFunctionElementContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.SelectStarElementContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.SimpleIdContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.SimpleSelectContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.SpecificFunctionCallContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.SqlStatementContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.SqlStatementsContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.StraightJoinContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.StringLiteralContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.SubqueryTableItemContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.TableNameContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.TableSourceBaseContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.TableSourceContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.TableSourceItemContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.TableSourceNestedContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.TableSourcesContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.UidContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.UnionParenthesisContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.UnionParenthesisSelectContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.UnionSelectContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.UnionStatementContext;
import org.zjvis.dp.data.lineage.parser.ast.ColumnIdentifier;
import org.zjvis.dp.data.lineage.parser.ast.DataClause;
import org.zjvis.dp.data.lineage.parser.ast.FromClause;
import org.zjvis.dp.data.lineage.parser.ast.Identifier;
import org.zjvis.dp.data.lineage.parser.ast.InsertQuery;
import org.zjvis.dp.data.lineage.parser.ast.Literal;
import org.zjvis.dp.data.lineage.parser.ast.SelectStatement;
import org.zjvis.dp.data.lineage.parser.ast.SelectStatement.ModifierType;
import org.zjvis.dp.data.lineage.parser.ast.SelectUnionQuery;
import org.zjvis.dp.data.lineage.parser.ast.TableIdentifier;
import org.zjvis.dp.data.lineage.parser.ast.expr.ColumnExpr;
import org.zjvis.dp.data.lineage.parser.ast.expr.FunctionColumnExpr;
import org.zjvis.dp.data.lineage.parser.ast.expr.JoinExpr;
import org.zjvis.dp.data.lineage.parser.ast.expr.TableExpr;

/**
 * @author zhouyu
 * @create 2023-05-19 17:13
 */
public class MySqlVisitor extends MySqlParserBaseVisitor{


    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitRoot(RootContext ctx) {
        return visit(ctx.sqlStatements());
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitSqlStatements(SqlStatementsContext ctx) {
        //todo 目前一条语句一条语句进行翻译
        return visit(ctx.sqlStatement().get(0));
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitSqlStatement(SqlStatementContext ctx) {
        if(null != ctx.ddlStatement()) {
            return visit(ctx.ddlStatement());
        }

        if(null != ctx.dmlStatement()) {
            return visit(ctx.dmlStatement());
        }

        if(null != ctx.transactionStatement()) {
            return visit(ctx.transactionStatement());
        }

        if(null != ctx.replicationStatement()) {
            return visit(ctx.replicationStatement());
        }

        if(null != ctx.preparedStatement()) {
            return visit(ctx.preparedStatement());
        }

        if(null != ctx.administrationStatement()) {
            return visit(ctx.administrationStatement());
        }

        if(null != ctx.utilityStatement()) {
            return visit(ctx.utilityStatement());
        }

        return null;
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitInsertStatement(InsertStatementContext ctx) {
        TableIdentifier tableIdentifier = (TableIdentifier) visit(ctx.tableName());
        List<Identifier> columnIdentifierList = Lists.newArrayList();
        if(null != ctx.columns) {
           columnIdentifierList.addAll((List<ColumnIdentifier>)visit(ctx.columns));
        }

        DataClause dataClause = (DataClause)visit(ctx.insertStatementValue());

        return InsertQuery.createTable(tableIdentifier, columnIdentifierList, dataClause);
    }


    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitInsertStatementValue(InsertStatementValueContext ctx) {
        if(null != ctx.selectStatement()) {
            SelectUnionQuery selectUnionQuery = (SelectUnionQuery) visit(ctx.selectStatement());
            return DataClause.createSelect(selectUnionQuery);
        }

        if(null != ctx.insertFormat) {
            int dataOffset = ctx.getStop().getStopIndex() + 1;
            DataClause.createValues(dataOffset);
        }

        return null;
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitSimpleSelect(SimpleSelectContext ctx) {
        SelectUnionQuery selectUnionQuery = new SelectUnionQuery();
        selectUnionQuery.appendSelect((SelectStatement)visit(ctx.querySpecification()));
        return selectUnionQuery;
    }





    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitParenthesisSelect(ParenthesisSelectContext ctx) {
        SelectUnionQuery selectUnionQuery = new SelectUnionQuery();
        selectUnionQuery.appendSelect((SelectStatement)visit(ctx.queryExpression()));
        return selectUnionQuery;
    }




    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitUnionSelect(UnionSelectContext ctx) {
        SelectUnionQuery selectUnionQuery = new SelectUnionQuery();
        selectUnionQuery.appendSelect((SelectStatement) visit(ctx.querySpecificationNointo()));
        for(UnionStatementContext unionStatementContext : ctx.unionStatement()) {
            if(unionStatementContext.queryExpressionNointo() != null) {
                selectUnionQuery.appendSelect((SelectStatement) visit(unionStatementContext.queryExpressionNointo()));
            }

            if(unionStatementContext.querySpecificationNointo() != null) {
                selectUnionQuery.appendSelect((SelectStatement) visit(unionStatementContext.querySpecificationNointo()));
            }
        }

        return selectUnionQuery;
    }


    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitUnionParenthesisSelect(UnionParenthesisSelectContext ctx) {
        SelectUnionQuery selectUnionQuery = new SelectUnionQuery();
        selectUnionQuery.appendSelect((SelectStatement) visit(ctx.queryExpressionNointo()));
        for(UnionParenthesisContext unionParenthesisContext : ctx.unionParenthesis()) {
            if(unionParenthesisContext.queryExpressionNointo() != null) {
                selectUnionQuery.appendSelect((SelectStatement) visit(unionParenthesisContext.queryExpressionNointo()));
            }
        }
        return selectUnionQuery;
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitQueryExpression(QueryExpressionContext ctx) {
        if(null != ctx.queryExpression()) {
            return visit(ctx.queryExpression());
        }

        if(null != ctx.querySpecification()) {
            return visit(ctx.querySpecification());
        }

        return null;
    }

    @Override
    public Object visitQueryExpressionNointo(QueryExpressionNointoContext ctx) {
        if(null != ctx.queryExpressionNointo()) {
            return visit(ctx.queryExpressionNointo());
        }

        if(null != ctx.querySpecificationNointo()) {
            return visit(ctx.querySpecificationNointo());
        }

        return null;
    }






    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     * querySpecification 与 querySpecificationNointo目前同等处理
     */
    @Override
    public Object visitQuerySpecification(QuerySpecificationContext ctx) {
        return visitQuerySpecificationOrNointo(ctx.selectElements(), ctx.fromClause());
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    /**
     * querySpecificationNointo
     *     : SELECT selectSpec* selectElements
     *       fromClause groupByClause? havingClause? windowClause? orderByClause? limitClause?
     *     ;
     * @param ctx the parse tree
     * @return
     */
    @Override
    public Object visitQuerySpecificationNointo(QuerySpecificationNointoContext ctx) {
        return visitQuerySpecificationOrNointo(ctx.selectElements(), ctx.fromClause());
    }

    private SelectStatement visitQuerySpecificationOrNointo(
            SelectElementsContext selectElementsContext,
            FromClauseContext fromClauseContext
    ) {
        List<ColumnExpr> columnExprList = (List<ColumnExpr>) visit(selectElementsContext);
        SelectStatement selectStatement = new SelectStatement(true, ModifierType.NONE, true, Lists.newArrayList());
        selectStatement.setExprs(columnExprList);
        if(null != fromClauseContext) {
            selectStatement.setFromClause((FromClause)visitFromClause(fromClauseContext));
        }
        return selectStatement;
    }


    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitSelectElements(SelectElementsContext ctx) {
        List<ColumnExpr> list = Lists.newArrayList();
        if(null != ctx.star) {
            //expectSingleColumn
           list.add(ColumnExpr.createAsterisk(null, Boolean.TRUE));
        }
        if(CollectionUtils.isNotEmpty(ctx.selectElement())) {
            list.addAll(ctx.selectElement().stream()
                    .map(selectElementContext -> (ColumnExpr) visit(selectElementContext))
                    .collect(Collectors.toList())
            );
        }
        return list;
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitSelectStarElement(SelectStarElementContext ctx) {
        String[] strArr = ((Identifier)visit(ctx.fullId())).getName().split("\\.");
        TableIdentifier tableIdentifier;
        if(strArr.length == 1) {
            tableIdentifier = new TableIdentifier(null, new Identifier(strArr[0]));
        } else {
            tableIdentifier = new TableIdentifier(new Identifier(strArr[0]), new Identifier(strArr[1]));
        }
        return ColumnExpr.createAsterisk(tableIdentifier, Boolean.TRUE);
    }


    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitSelectColumnElement(SelectColumnElementContext ctx) {
        ColumnIdentifier columnIdentifier = (ColumnIdentifier) visit(ctx.fullColumnName());
        if(null != ctx.uid()) {
           return ColumnExpr.createAlias(ColumnExpr.createIdentifier(columnIdentifier), (Identifier) visit(ctx.uid()));
        } else {
            return ColumnExpr.createIdentifier(columnIdentifier);
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitSelectFunctionElement(SelectFunctionElementContext ctx) {
        FunctionColumnExpr functionColumnExpr = (FunctionColumnExpr)visit(ctx.functionCall());
        if(null != ctx.uid()) {
            return ColumnExpr.createAlias(functionColumnExpr, (Identifier) visit(ctx.uid()));
        } else {
            return functionColumnExpr;
        }
    }


    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitSelectExpressionElement(SelectExpressionElementContext ctx) {
        return super.visitSelectExpressionElement(ctx);
    }


    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitSpecificFunctionCall(SpecificFunctionCallContext ctx) {
        return visit(ctx.specificFunction());
    }


    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitExtractFunctionCall(ExtractFunctionCallContext ctx) {
        List<ColumnExpr> args = Lists.newArrayList();
        Identifier nameIdentifier = (Identifier)visit(ctx.intervalType());
        if(null != ctx.sourceString) {
            Identifier identifier = (Identifier)visit(ctx.sourceString);
            args.add(ColumnExpr.createLiteral(Literal.createString(identifier.getName())));
        }

        if(null != ctx.sourceExpression) {
            List<ColumnIdentifier> columnIdentifiers = (List<ColumnIdentifier>) visit(ctx.sourceExpression);
            args.addAll(
                    columnIdentifiers.stream()
                            .map(ColumnExpr::createIdentifier)
                            .collect(Collectors.toList())
            );
        }
        return ColumnExpr.createFunction(nameIdentifier, null,args);
    }


    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitScalarFunctionCall(ScalarFunctionCallContext ctx) {
        Identifier functionName = (Identifier)visit(ctx.scalarFunctionName());
        List<ColumnExpr> args = null;
        if(null != ctx.functionArgs()) {
            args = (List<ColumnExpr>)visit(ctx.functionArgs());
        }
        return ColumnExpr.createFunction(functionName, null, args);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitNotExpression(NotExpressionContext ctx) {
        return super.visitNotExpression(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitLogicalExpression(LogicalExpressionContext ctx) {
        return super.visitLogicalExpression(ctx);
    }


    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitPredicateExpression(PredicateExpressionContext ctx) {
        return visit(ctx.predicate());
    }


    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitExpressionAtomPredicate(ExpressionAtomPredicateContext ctx) {
        return visit(ctx.expressionAtom());
    }


    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitFullColumnNameExpressionAtom(FullColumnNameExpressionAtomContext ctx) {
        List<ColumnIdentifier> columnIdentifiers = Lists.newArrayList();
        columnIdentifiers.add((ColumnIdentifier) visit(ctx.fullColumnName()));
        return columnIdentifiers;
    }


    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitMathExpressionAtom(MathExpressionAtomContext ctx) {
        return ctx.expressionAtom().stream()
                .map(expressionAtomContext -> (List<ColumnIdentifier>) visit(expressionAtomContext))
                .filter(CollectionUtils::isNotEmpty)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }


    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitBitExpressionAtom(BitExpressionAtomContext ctx) {
        return ctx.expressionAtom().stream()
                .map(expressionAtomContext -> (List<ColumnIdentifier>) visit(expressionAtomContext))
                .filter(CollectionUtils::isNotEmpty)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }


    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitJsonExpressionAtom(JsonExpressionAtomContext ctx) {
        return ctx.expressionAtom().stream()
                .map(expressionAtomContext -> (List<ColumnIdentifier>) visit(expressionAtomContext))
                .filter(CollectionUtils::isNotEmpty)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }



    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitIntervalType(IntervalTypeContext ctx) {
        if(null != ctx.intervalTypeBase()) {
            return visit(ctx.intervalTypeBase());
        }

        if(null != ctx.YEAR()) {
            return visit(ctx.YEAR());
        }

        if(null != ctx.YEAR_MONTH()) {
            return visit(ctx.YEAR_MONTH());
        }

        if(null != ctx.DAY_HOUR()) {
            return visit(ctx.DAY_HOUR());
        }

        if(null != ctx.DAY_MINUTE()) {
            return visit(ctx.DAY_MINUTE());
        }

        if(null != ctx.DAY_SECOND()) {
            return visit(ctx.DAY_SECOND());
        }

        if(null != ctx.HOUR_MINUTE()) {
            return visit(ctx.HOUR_MINUTE());
        }

        if(null != ctx.HOUR_SECOND()) {
            return visit(ctx.HOUR_SECOND());
        }

        if(null != ctx.MINUTE_SECOND()) {
            return visit(ctx.MINUTE_SECOND());
        }

        if(null != ctx.SECOND_MICROSECOND()) {
            return visit(ctx.SECOND_MICROSECOND());
        }

        if(null != ctx.MINUTE_MICROSECOND()) {
            return visit(ctx.MINUTE_MICROSECOND());
        }

        if(null != ctx.HOUR_MICROSECOND()) {
            return visit(ctx.HOUR_MICROSECOND());
        }

        if(null != ctx.DAY_MICROSECOND()) {
            return visit(ctx.DAY_MICROSECOND());
        }

        return null;
    }


    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitIntervalTypeBase(IntervalTypeBaseContext ctx) {
        if(null != ctx.QUARTER()) {
            return visit(ctx.QUARTER());
        }

        if(null != ctx.MONTH()) {
            return visit(ctx.MONTH());
        }

        if(null != ctx.DAY()) {
            return visit(ctx.DAY());
        }

        if(null != ctx.HOUR()) {
            return visit(ctx.HOUR());
        }

        if(null != ctx.MINUTE()) {
            return visit(ctx.MINUTE());
        }

        if(null != ctx.WEEK()) {
            return visit(ctx.WEEK());
        }

        if(null != ctx.SECOND()) {
            return visit(ctx.SECOND());
        }

        if(null != ctx.MICROSECOND()) {
            return visit(ctx.MICROSECOND());
        }

        return null;
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
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
        if(null != ctx.uid()) {
            Identifier firstIdentifier = (Identifier)visit(ctx.uid());
            Identifier secondIdentifier;
            Identifier thirdIdentifier;
            if(CollectionUtils.isNotEmpty(ctx.dottedId())) {
                secondIdentifier = (Identifier)visit(ctx.dottedId(0));
                if(ctx.dottedId().size() == 1) {
                    tableIdentifier = new TableIdentifier(null, firstIdentifier);
                    columnIdentifier = new ColumnIdentifier(tableIdentifier, secondIdentifier);
                } else {
                    thirdIdentifier = (Identifier)visit(ctx.dottedId(1));
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


    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitFromClause(FromClauseContext ctx) {
        JoinExpr joinExpr = (JoinExpr)visit(ctx.tableSources());
        return new FromClause(joinExpr);
    }


    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitTableSources(TableSourcesContext ctx) {
        //todo 目前不支持from多张表的语句, 即from a, b, c
        TableSourceContext tableSourceContext = ctx.tableSource(0);
        return visit(tableSourceContext);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitTableSourceBase(TableSourceBaseContext ctx) {
        return visitTableSource(ctx.tableSourceItem(), ctx.joinPart());
    }


    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitTableSourceNested(TableSourceNestedContext ctx) {
        return visitTableSource(ctx.tableSourceItem(), ctx.joinPart());
    }

    private JoinExpr visitTableSource(TableSourceItemContext tableSourceItemContext, List<JoinPartContext> joinPartContextList) {
        JoinExpr singleJoinExpr = JoinExpr.createTableExpr((TableExpr) visit(tableSourceItemContext), null, true);
        if(null == joinPartContextList) {
            return singleJoinExpr;
        }

        List<JoinExpr> joinExprList = joinPartContextList.stream()
                .map(joinPartContext -> (TableExpr)visit(joinPartContext))
                .map(tableExpr -> JoinExpr.createTableExpr(tableExpr, null, true))
                .collect(Collectors.toList());

        //仿照clickhouse串成链
        JoinExpr leftJoinExpr = singleJoinExpr;
        for (JoinExpr joinExpr : joinExprList) {
            leftJoinExpr = JoinExpr.createJoinOp(null, leftJoinExpr, joinExpr, null, null);
        }

        return leftJoinExpr;
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitFunctionArgs(FunctionArgsContext ctx) {
        List<ColumnExpr> result = Lists.newArrayList();
        if(CollectionUtils.isNotEmpty(ctx.fullColumnName())) {
            result.addAll(
                    ctx.fullColumnName().stream()
                            .map(this::visit)
                            .map(element -> (ColumnIdentifier) element)
                            .map(ColumnExpr::createIdentifier)
                            .collect(Collectors.toList())
            );
        }

        if(CollectionUtils.isNotEmpty(ctx.functionCall())) {
            result.addAll(
                    ctx.functionCall().stream()
                            .map(this::visit)
                            .map(element -> (FunctionColumnExpr) element)
                            .map(FunctionColumnExpr::getArgs)
                            .flatMap(Collection::stream)
                            .collect(Collectors.toList())
            );
        }

        if(CollectionUtils.isNotEmpty(ctx.expression())) {
            result.addAll(
                    ctx.expression().stream()
                            .map(this::visit)
                            .map(element -> (List<ColumnIdentifier>) element)
                            .flatMap(Collection::stream)
                            .map(ColumnExpr::createIdentifier)
                            .collect(Collectors.toList())
            );
        }

        return CollectionUtils.isEmpty(result) ? null : result;
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitInnerJoin(InnerJoinContext ctx) {
        return visit(ctx.tableSourceItem());
    }


    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitStraightJoin(StraightJoinContext ctx) {
        return visit(ctx.tableSourceItem());
    }


    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitOuterJoin(OuterJoinContext ctx) {
        return visit(ctx.tableSourceItem());
    }


    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitNaturalJoin(NaturalJoinContext ctx) {
        return super.visitNaturalJoin(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitAtomTableItem(AtomTableItemContext ctx) {
        TableIdentifier tableIdentifier = (TableIdentifier) visit(ctx.tableName());
        return TableExpr.createIdentifier(tableIdentifier);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitSubqueryTableItem(SubqueryTableItemContext ctx) {
        SelectUnionQuery selectUnionQuery = null;
        if(null != ctx.parenthesisSubquery) {
            selectUnionQuery = (SelectUnionQuery) visit(ctx.parenthesisSubquery);
        }

        if(null != ctx.selectStatement()) {
            selectUnionQuery = (SelectUnionQuery) visit(ctx.selectStatement());
        }

        Identifier identifier = (Identifier)visit(ctx.alias);
        return TableExpr.createAlias(TableExpr.createSubquery(selectUnionQuery), identifier);
    }


    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitScalarFunctionName(ScalarFunctionNameContext ctx) {
        if(null != ctx.ASCII()) {
            return visit(ctx.ASCII());
        }

        if(null != ctx.CURDATE()) {
            return visit(ctx.CURDATE());
        }

        if(null != ctx.CURRENT_DATE()) {
            return visit(ctx.CURRENT_DATE());
        }

        if(null != ctx.CURRENT_TIME()) {
            return visit(ctx.CURRENT_TIME());
        }

        if(null != ctx.CURRENT_TIMESTAMP()) {
            return visit(ctx.CURRENT_TIMESTAMP());
        }

        if(null != ctx.CURTIME()) {
            return visit(ctx.CURTIME());
        }

        if(null != ctx.DATE_ADD()) {
            return visit(ctx.DATE_ADD());
        }

        if(null != ctx.DATE_SUB()) {
            return visit(ctx.DATE_SUB());
        }

        if(null != ctx.IF()) {
            return visit(ctx.IF());
        }

        if(null != ctx.INSERT()) {
            return visit(ctx.INSERT());
        }

        if(null != ctx.LOCALTIME()) {
            return visit(ctx.LOCALTIME());
        }

        if(null != ctx.LOCALTIMESTAMP()) {
            return visit(ctx.LOCALTIMESTAMP());
        }

        if(null != ctx.MID()) {
            return visit(ctx.MID());
        }

        if(null != ctx.NOW()) {
            return visit(ctx.NOW());
        }

        if(null != ctx.REPLACE()) {
            return ctx.REPLACE();
        }

        if(null != ctx.SUBSTR()) {
            return ctx.SUBSTR();
        }

        if(null != ctx.SUBSTRING()) {
            return ctx.SUBSTRING();
        }

        if(null != ctx.SYSDATE()) {
            return ctx.SYSDATE();
        }

        if(null != ctx.TRIM()) {
            return ctx.TRIM();
        }

        if(null != ctx.UTC_DATE()) {
            return ctx.UTC_DATE();
        }

        if(null != ctx.UTC_TIME()) {
            return ctx.UTC_TIME();
        }

        if(null != ctx.UTC_TIMESTAMP()) {
            return ctx.UTC_TIMESTAMP();
        }

        if(null != ctx.functionNameBase()) {
            return visit(ctx.functionNameBase());
        }

        return null;
    }


    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitFunctionNameBase(FunctionNameBaseContext ctx) {
        return super.visitFunctionNameBase(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitStringLiteral(StringLiteralContext ctx) {
        //todo 默认返回第一个，后面修改
        return visit(ctx.STRING_LITERAL(0));
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitBooleanLiteral(BooleanLiteralContext ctx) {
        if(null != ctx.TRUE()) {
            return visit(ctx.TRUE());
        }

        if(null != ctx.FALSE()) {
            return visit(ctx.FALSE());
        }

        return null;
    }


    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitDecimalLiteral(DecimalLiteralContext ctx) {
        if(null != ctx.DECIMAL_LITERAL()) {
            return visit(ctx.DECIMAL_LITERAL());
        }

        if(null != ctx.ZERO_DECIMAL()) {
            return visit(ctx.ZERO_DECIMAL());
        }

        if(null != ctx.ONE_DECIMAL()) {
            return visit(ctx.ONE_DECIMAL());
        }

        if(null != ctx.TWO_DECIMAL()) {
            return visit(ctx.TWO_DECIMAL());
        }

        if(null != ctx.REAL_LITERAL()) {
            return visit(ctx.REAL_LITERAL());
        }

        return null;
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitHexadecimalLiteral(HexadecimalLiteralContext ctx) {
        if(null != ctx.HEXADECIMAL_LITERAL()) {
            return visit(ctx.HEXADECIMAL_LITERAL());
        }

        if(null != ctx.STRING_CHARSET_NAME()) {
            return visit(ctx.STRING_CHARSET_NAME());
        }

        return null;
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitTableName(TableNameContext ctx) {
        Identifier identifier = (Identifier)visit(ctx.fullId());
        TableIdentifier tableIdentifier;
        String[] strArr = identifier.getName().split("\\.");
        if(strArr.length == 2) {
            tableIdentifier = new TableIdentifier(new Identifier(strArr[0]), new Identifier(strArr[1]));
        } else {
            tableIdentifier = new TableIdentifier(null, new Identifier(strArr[0]));
        }
        return tableIdentifier;
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitFullId(FullIdContext ctx) {
        StringBuilder result =  new StringBuilder();
        if(CollectionUtils.isNotEmpty(ctx.uid())) {
            if(ctx.uid().size() == 2) {
                String uidFirst = ((Identifier)visit(ctx.uid(0))).getName();
                String uidSecond = ((Identifier)visit(ctx.uid(1))).getName();
                result.append(uidFirst).append(".").append(uidSecond);
            } else {
                String uidFirst = ((Identifier)visit(ctx.uid(0))).getName();
                result.append(uidFirst);
            }
        }
        if(null != ctx.DOT_ID()) {
            result.append((String)visit(ctx.DOT_ID()));
        }
        return new Identifier(result.toString());
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitUid(UidContext ctx) {
        if(null != ctx.simpleId()) {
            return visit(ctx.simpleId());
        }
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitSimpleId(SimpleIdContext ctx) {
        if(null != ctx.ID()) {
            return visit(ctx.ID());
        }

        return null;
    }



    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitDottedId(DottedIdContext ctx) {
        if(null != ctx.DOT_ID()) {
            Identifier identifier = (Identifier)visit(ctx.DOT_ID());
            return new Identifier(identifier.getName().substring(1));
        }
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of
     * {@link #defaultResult defaultResult}.</p>
     *
     * @param node
     */
    @Override
    public Object visitTerminal(TerminalNode node) {
        return new Identifier(node.getSymbol().getText());
    }


    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public Object visitConstant(ConstantContext ctx) {
        if(null != ctx.stringLiteral()) {
            return visit(ctx.stringLiteral());
        }

        if(null != ctx.decimalLiteral()) {
            return visit(ctx.decimalLiteral());
        }

        if(null != ctx.hexadecimalLiteral()) {
            return visit(ctx.hexadecimalLiteral());
        }

        if(null != ctx.booleanLiteral()) {
            return visit(ctx.booleanLiteral());
        }

        if(null != ctx.REAL_LITERAL()) {
            return visit(ctx.REAL_LITERAL());
        }

        if(null != ctx.BIT_STRING()) {
            return visit(ctx.BIT_STRING());
        }

        if(null != ctx.NULL_LITERAL()) {
            return visit(ctx.NULL_LITERAL());
        }

        if(null != ctx.NULL_SPEC_LITERAL()) {
            return visit(ctx.NULL_SPEC_LITERAL());
        }

        return null;
    }
}
