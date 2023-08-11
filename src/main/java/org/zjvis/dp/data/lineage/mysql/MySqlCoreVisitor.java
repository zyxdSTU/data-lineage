package org.zjvis.dp.data.lineage.mysql;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.AtomTableItemContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.BinaryComparisonPredicateContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.BitExpressionAtomContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.CaseFuncAlternativeContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.CaseFunctionCallContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.ConstantExpressionAtomContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.ExpressionAtomPredicateContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.ExtractFunctionCallContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.FromClauseContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.FullColumnNameExpressionAtomContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.FunctionArgContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.FunctionArgsContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.InsertStatementContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.InsertStatementValueContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.JoinPartContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.JsonExpressionAtomContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.MathExpressionAtomContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.ParenthesisSelectContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.QueryExpressionContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.QueryExpressionNointoContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.QuerySpecificationContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.QuerySpecificationNointoContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.RootContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.ScalarFunctionCallContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.SelectColumnElementContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.SelectElementsContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.SelectExpressionElementContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.SelectFunctionElementContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.SelectStarElementContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.SimpleSelectContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.SpecificFunctionCallContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.SqlStatementsContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.SubqueryTableItemContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.TableSourceBaseContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.TableSourceContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.TableSourceItemContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.TableSourceNestedContext;
import org.zjvis.dp.data.lineage.mysql.MySqlParser.TableSourcesContext;
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
import org.zjvis.dp.data.lineage.parser.ast.expr.IdentifierColumnExpr;
import org.zjvis.dp.data.lineage.parser.ast.expr.JoinExpr;
import org.zjvis.dp.data.lineage.parser.ast.expr.TableExpr;
import org.zjvis.dp.data.lineage.util.DataLineageUtil;

/**
 * @author zhouyu
 * @create 2023-06-16 14:03
 */
public class MySqlCoreVisitor extends MySqlVisitor{
    @Override
    public Object visitRoot(RootContext ctx) {
        return visit(ctx.sqlStatements());
    }


    @Override
    public Object visitSqlStatements(SqlStatementsContext ctx) {
        //todo 目前一条语句一条语句进行翻译
        return visit(ctx.sqlStatement().get(0));
    }


    @Override
    public Object visitInsertStatement(InsertStatementContext ctx) {
        TableIdentifier tableIdentifier = (TableIdentifier) visit(ctx.tableName());
        List<Identifier> columnIdentifierList = Lists.newArrayList();
        if(null != ctx.columns) {
            columnIdentifierList.addAll(DataLineageUtil.castList(visit(ctx.columns), ColumnIdentifier.class));
        }

        DataClause dataClause = (DataClause)visit(ctx.insertStatementValue());

        return InsertQuery.createTable(tableIdentifier, columnIdentifierList, dataClause);
    }


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

    @Override
    public Object visitSimpleSelect(SimpleSelectContext ctx) {
        SelectUnionQuery selectUnionQuery = new SelectUnionQuery();
        selectUnionQuery.appendSelect((SelectStatement)visit(ctx.querySpecification()));
        return selectUnionQuery;
    }

    @Override
    public Object visitParenthesisSelect(ParenthesisSelectContext ctx) {
        SelectUnionQuery selectUnionQuery = new SelectUnionQuery();
        selectUnionQuery.appendSelect((SelectStatement)visit(ctx.queryExpression()));
        return selectUnionQuery;
    }

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

        if(null != ctx.querySpecification()) {
            selectUnionQuery.appendSelect((SelectStatement) visit(ctx.querySpecification()));
        }

        if(null != ctx.queryExpression()) {
            selectUnionQuery.appendSelect((SelectStatement) visit(ctx.queryExpression()));
        }

        return selectUnionQuery;
    }


    @Override
    public Object visitUnionParenthesisSelect(UnionParenthesisSelectContext ctx) {
        SelectUnionQuery selectUnionQuery = new SelectUnionQuery();
        selectUnionQuery.appendSelect((SelectStatement) visit(ctx.queryExpressionNointo()));
        for(UnionParenthesisContext unionParenthesisContext : ctx.unionParenthesis()) {
            if(unionParenthesisContext.queryExpressionNointo() != null) {
                selectUnionQuery.appendSelect((SelectStatement) visit(unionParenthesisContext.queryExpressionNointo()));
            }
        }

        if(null != ctx.queryExpression()) {
            selectUnionQuery.appendSelect((SelectStatement) visit(ctx.queryExpression()));
        }

        return selectUnionQuery;
    }


    @Override
    public Object visitQueryExpression(QueryExpressionContext ctx) {
        if(null != ctx.queryExpression()) {
            return visit(ctx.queryExpression());
        }else {
            return visit(ctx.querySpecification());
        }
    }


    @Override
    public Object visitQueryExpressionNointo(QueryExpressionNointoContext ctx) {
        if (null != ctx.queryExpressionNointo()) {
            return visit(ctx.queryExpressionNointo());
        } else {
            return visit(ctx.querySpecificationNointo());
        }
    }


    @Override
    public Object visitQuerySpecification(QuerySpecificationContext ctx) {
        return visitQuerySpecificationOrNointo(ctx.selectElements(), ctx.fromClause());
    }


    @Override
    public Object visitQuerySpecificationNointo(QuerySpecificationNointoContext ctx) {
        return visitQuerySpecificationOrNointo(ctx.selectElements(), ctx.fromClause());
    }

    private SelectStatement visitQuerySpecificationOrNointo(
            SelectElementsContext selectElementsContext,
            FromClauseContext fromClauseContext
    ) {
        List<ColumnExpr> columnExprList = DataLineageUtil.castList(visit(selectElementsContext), ColumnExpr.class);
        SelectStatement selectStatement = new SelectStatement(true, ModifierType.NONE, true, Lists.newArrayList());
        selectStatement.setExprs(columnExprList);
        if(null != fromClauseContext) {
            selectStatement.setFromClause((FromClause)visitFromClause(fromClauseContext));
        }
        return selectStatement;
    }


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


    @Override
    public Object visitSelectColumnElement(SelectColumnElementContext ctx) {
        ColumnIdentifier columnIdentifier = (ColumnIdentifier) visit(ctx.fullColumnName());
        if(null != ctx.uid()) {
            return ColumnExpr.createAlias(ColumnExpr.createIdentifier(columnIdentifier), (Identifier) visit(ctx.uid()));
        } else {
            return ColumnExpr.createIdentifier(columnIdentifier);
        }
    }


    @Override
    public Object visitSelectFunctionElement(SelectFunctionElementContext ctx) {
        FunctionColumnExpr functionColumnExpr = (FunctionColumnExpr)visit(ctx.functionCall());
        if(null != ctx.uid()) {
            return ColumnExpr.createAlias(functionColumnExpr, (Identifier) visit(ctx.uid()));
        } else {
            return functionColumnExpr;
        }
    }

    @Override
    public Object visitSelectExpressionElement(SelectExpressionElementContext ctx) {
        List<ColumnIdentifier> columnIdentifiers = DataLineageUtil.castList(visit(ctx.expression()), ColumnIdentifier.class);
        List<ColumnExpr> columnExprList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(columnIdentifiers)) {
            columnExprList.addAll(
                    columnIdentifiers.stream()
                            .map(ColumnExpr::createIdentifier)
                            .collect(Collectors.toList())
            );
        }
        FunctionColumnExpr functionColumnExpr = FunctionColumnExpr.createFunction(
                new Identifier("expression"),
                null,
                columnExprList
        );

        if(null != ctx.uid()) {
            return ColumnExpr.createAlias(functionColumnExpr, (Identifier) visit(ctx.uid()));
        } else {
            return functionColumnExpr;
        }
    }


    @Override
    public Object visitExtractFunctionCall(ExtractFunctionCallContext ctx) {
        List<ColumnExpr> args = Lists.newArrayList();
        Identifier nameIdentifier = (Identifier)visit(ctx.intervalType());
        if(null != ctx.sourceString) {
            Identifier identifier = (Identifier)visit(ctx.sourceString);
            args.add(ColumnExpr.createLiteral(Literal.createString(identifier.getName())));
        }

        if(null != ctx.sourceExpression) {
            List<ColumnIdentifier> columnIdentifiers = DataLineageUtil.castList(visit(ctx.sourceExpression), ColumnIdentifier.class);
            args.addAll(
                    columnIdentifiers.stream()
                            .map(ColumnExpr::createIdentifier)
                            .collect(Collectors.toList())
            );
        }
        return ColumnExpr.createFunction(nameIdentifier, null,args);
    }


    @Override
    public Object visitSpecificFunctionCall(SpecificFunctionCallContext ctx) {
        return visit(ctx.specificFunction());
    }


    @Override
    public Object visitCaseFunctionCall(CaseFunctionCallContext ctx) {
        List<ColumnIdentifier> columnIdentifiers = Lists.newArrayList();
        if(CollectionUtils.isNotEmpty(ctx.caseFuncAlternative())) {
            columnIdentifiers.addAll(ctx.caseFuncAlternative().stream()
                    .map(this::visit)
                    .filter(Objects::nonNull)
                    .map(element -> DataLineageUtil.castList(element, ColumnIdentifier.class))
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList())
            );
        }

        if(Objects.nonNull(ctx.functionArg())) {
            Object object = visit(ctx.functionArg());
            if(Objects.nonNull(object)) {
                columnIdentifiers.addAll(DataLineageUtil.castList(object, ColumnIdentifier.class));
            }
        }
        List<ColumnExpr> columnExprList = columnIdentifiers.stream()
                .map(ColumnExpr::createIdentifier)
                .collect(Collectors.toList());

        return ColumnExpr.createFunction(new Identifier("case"), null, columnExprList);
    }


    @Override
    public Object visitCaseFuncAlternative(CaseFuncAlternativeContext ctx) {
        if(CollectionUtils.isNotEmpty(ctx.functionArg())) {
            return ctx.functionArg().stream()
                    .map(this::visit)
                    .filter(Objects::nonNull)
                    .map(element -> DataLineageUtil.castList(element, ColumnIdentifier.class))
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
        } else {
            return null;
        }
    }



    @Override
    public Object visitScalarFunctionCall(ScalarFunctionCallContext ctx) {
        Identifier functionName = (Identifier)visit(ctx.scalarFunctionName());
        List<ColumnExpr> args = null;
        if(null != ctx.functionArgs()) {
            args = DataLineageUtil.castList(visit(ctx.functionArgs()), ColumnIdentifier.class).stream()
                    .map(ColumnExpr::createIdentifier)
                    .collect(Collectors.toList());
        }
        return ColumnExpr.createFunction(functionName, null, args);
    }

    @Override
    public Object visitFunctionArgs(FunctionArgsContext ctx) {
        List<ColumnIdentifier> result = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(ctx.fullColumnName())) {
            result.addAll(
                    ctx.fullColumnName().stream()
                            .map(this::visit)
                            .filter(Objects::nonNull)
                            .map(element -> (ColumnIdentifier) element)
                            .collect(Collectors.toList())
            );
        }

        if (CollectionUtils.isNotEmpty(ctx.functionCall())) {
            result.addAll(
                    ctx.functionCall().stream()
                            .map(this::visit)
                            .filter(Objects::nonNull)
                            .map(element -> (FunctionColumnExpr) element)
                            .map(FunctionColumnExpr::getArgs)
                            .filter(CollectionUtils::isNotEmpty)
                            .flatMap(Collection::stream)
                            .map(element -> (IdentifierColumnExpr)element)
                            .map(IdentifierColumnExpr::getIdentifier)
                            .collect(Collectors.toList())
            );
        }

        if (CollectionUtils.isNotEmpty(ctx.expression())) {
            result.addAll(
                    ctx.expression().stream()
                            .map(this::visit)
                            .map(element -> DataLineageUtil.castList(element, ColumnIdentifier.class))
                            .flatMap(Collection::stream)
                            .collect(Collectors.toList())
            );
        }

        return CollectionUtils.isEmpty(result) ? null : result;
    }


    @Override
    public Object visitFunctionArg(FunctionArgContext ctx) {
        List<ColumnIdentifier> result = Lists.newArrayList();
        if(Objects.nonNull(ctx.fullColumnName())) {
            result.add((ColumnIdentifier)visit(ctx.fullColumnName()));
        }

        if(Objects.nonNull(ctx.functionCall())) {
            Object object = visit(ctx.functionCall());
            if(Objects.nonNull(object)) {
                FunctionColumnExpr functionColumnExpr = (FunctionColumnExpr) object;
                List<IdentifierColumnExpr> identifierColumnExprList =
                        DataLineageUtil.castList(functionColumnExpr.getArgs(), IdentifierColumnExpr.class);
                if (CollectionUtils.isNotEmpty(identifierColumnExprList)) {
                    result.addAll(
                            identifierColumnExprList.stream()
                                    .map(IdentifierColumnExpr::getIdentifier)
                                    .collect(Collectors.toList())
                    );
                }
            }

        }

        if(Objects.nonNull(ctx.expression())) {
            Object object = visit(ctx.expression());
            if(Objects.nonNull(object)) {
                result.addAll(DataLineageUtil.castList(object, ColumnIdentifier.class));
            }
        }

        return CollectionUtils.isEmpty(result) ? null : result;
    }

    @Override
    public Object visitExpressionAtomPredicate(ExpressionAtomPredicateContext ctx) {
        return visit(ctx.expressionAtom());
    }


    @Override
    public Object visitBinaryComparisonPredicate(BinaryComparisonPredicateContext ctx) {
        if(CollectionUtils.isNotEmpty(ctx.predicate())) {
            return ctx.predicate().stream()
                    .map(this::visit)
                    .filter(Objects::nonNull)
                    .map(element -> DataLineageUtil.castList(element, ColumnIdentifier.class))
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public Object visitFullColumnNameExpressionAtom(FullColumnNameExpressionAtomContext ctx) {
        List<ColumnIdentifier> columnIdentifiers = Lists.newArrayList();
        columnIdentifiers.add((ColumnIdentifier) visit(ctx.fullColumnName()));
        return columnIdentifiers;
    }



    @Override
    public Object visitMathExpressionAtom(MathExpressionAtomContext ctx) {
        return ctx.expressionAtom().stream()
                .map(expressionAtomContext -> (DataLineageUtil.castList(visit(expressionAtomContext), ColumnIdentifier.class)))
                .filter(CollectionUtils::isNotEmpty)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }


    @Override
    public Object visitConstantExpressionAtom(ConstantExpressionAtomContext ctx) {
        //常量不关注
        return null;
    }

    @Override
    public Object visitBitExpressionAtom(BitExpressionAtomContext ctx) {
        return ctx.expressionAtom().stream()
                .map(expressionAtomContext -> (DataLineageUtil.castList(visit(expressionAtomContext), ColumnIdentifier.class)))
                .filter(CollectionUtils::isNotEmpty)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }



    @Override
    public Object visitJsonExpressionAtom(JsonExpressionAtomContext ctx) {
        return ctx.expressionAtom().stream()
                .map(expressionAtomContext -> (DataLineageUtil.castList(visit(expressionAtomContext), ColumnIdentifier.class)))
                .filter(CollectionUtils::isNotEmpty)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    @Override
    public Object visitFromClause(FromClauseContext ctx) {
        JoinExpr joinExpr = (JoinExpr) visit(ctx.tableSources());
        return new FromClause(joinExpr);
    }


    @Override
    public Object visitTableSources(TableSourcesContext ctx) {
        //todo 目前不支持from多张表的语句, 即from a, b, c
        TableSourceContext tableSourceContext = ctx.tableSource(0);
        return visit(tableSourceContext);
    }


    @Override
    public Object visitTableSourceBase(TableSourceBaseContext ctx) {
        return visitTableSource(ctx.tableSourceItem(), ctx.joinPart());
    }


    @Override
    public Object visitTableSourceNested(TableSourceNestedContext ctx) {
        return visitTableSource(ctx.tableSourceItem(), ctx.joinPart());
    }

    private JoinExpr visitTableSource(TableSourceItemContext tableSourceItemContext, List<JoinPartContext> joinPartContextList) {
        JoinExpr singleJoinExpr = JoinExpr.createTableExpr((TableExpr) visit(tableSourceItemContext), null, true);
        if (null == joinPartContextList) {
            return singleJoinExpr;
        }

        List<JoinExpr> joinExprList = joinPartContextList.stream()
                .map(joinPartContext -> (TableExpr) visit(joinPartContext))
                .map(tableExpr -> JoinExpr.createTableExpr(tableExpr, null, true))
                .collect(Collectors.toList());

        //仿照clickhouse串成链
        JoinExpr leftJoinExpr = singleJoinExpr;
        for (JoinExpr joinExpr : joinExprList) {
            leftJoinExpr = JoinExpr.createJoinOp(null, leftJoinExpr, joinExpr, null, null);
        }

        return leftJoinExpr;
    }


    @Override
    public Object visitAtomTableItem(AtomTableItemContext ctx) {
        TableIdentifier tableIdentifier = (TableIdentifier) visit(ctx.tableName());
        return TableExpr.createIdentifier(tableIdentifier);
    }


    @Override
    public Object visitSubqueryTableItem(SubqueryTableItemContext ctx) {
        SelectUnionQuery selectUnionQuery = null;
        if (null != ctx.parenthesisSubquery) {
            selectUnionQuery = (SelectUnionQuery) visit(ctx.parenthesisSubquery);
        }

        if (null != ctx.selectStatement()) {
            selectUnionQuery = (SelectUnionQuery) visit(ctx.selectStatement());
        }

        Identifier identifier = (Identifier) visit(ctx.alias);
        return TableExpr.createAlias(TableExpr.createSubquery(selectUnionQuery), identifier);
    }
}
