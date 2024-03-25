package org.translateToSql.twoVL.visitors;

import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;
import org.translateToSql.utils.ChildPosition;
import org.translateToSql.utils.ExpressionUtils;
import org.translateToSql.utils.Parser;

import java.util.List;
import java.util.Objects;

import static org.translateToSql.utils.ExpressionUtils.*;

public class TrFExpressionVisitor extends TwoVLExpressionVisitor {

    private final TrTExpressionVisitor trTExpressionVisitor;

    public TrFExpressionVisitor(TrTExpressionVisitor trTExpressionVisitor){
        this.trTExpressionVisitor = trTExpressionVisitor;
    }

    @Override
    public void visit(Column column) {
        // tr_f(true) = false
        if (Objects.equals(column.getColumnName(), "TRUE")){
            column.setColumnName("FALSE");
        }
        // tr_f(false) = true
        else if (Objects.equals(column.getColumnName(), "FALSE")) {
            column.setColumnName("TRUE");}
    }

    @Override
    public void visit(IsNullExpression isNullExpression) {
        // tr_f(¬isnull(t)) = tr_t(isnull(t))
        if (isNullExpression.isNot()){
            isNullExpression.setNot(false);
            this.trTExpressionVisitor.setParentNode(this.getParentNode());
            isNullExpression.accept(this.trTExpressionVisitor);
        }
        // tr_f(isnull(t)) = ¬isnull(t)
        else {
            isNullExpression.setNot(true);
            this.setParentNode(isNullExpression, ChildPosition.LEFT);
            this.trTExpressionVisitor.setParentNode(this.getParentNode());
            isNullExpression.getLeftExpression().accept(this.trTExpressionVisitor);
        }
    }

    @Override
    public void visit(IsBooleanExpression isBooleanExpression) {
        // tr_f(¬θ) = tr_t(θ)
        if (isBooleanExpression.isNot()){
            isBooleanExpression.setNot(false);
            this.trTExpressionVisitor.setParentNode(this.getParentNode());
            isBooleanExpression.accept(this.trTExpressionVisitor);
        }
        // tr_f(θ) = ¬θ
        else {
            isBooleanExpression.setIsTrue(!isBooleanExpression.isTrue());
            this.setParentNode(isBooleanExpression, ChildPosition.LEFT);
            this.trTExpressionVisitor.setParentNode(this.getParentNode());
            isBooleanExpression.getLeftExpression().accept(this.trTExpressionVisitor);
        }
    }

    @Override
    public void visit(EqualsTo equalsTo) { handleComparisonExpression(equalsTo);}

    @Override
    public void visit(GreaterThan greaterThan){
        handleComparisonExpression(greaterThan);
    }

    @Override
    public void visit(GreaterThanEquals greaterThanEquals){
        handleComparisonExpression(greaterThanEquals);
    }

    @Override
    public void visit(MinorThan minorThan){
        handleComparisonExpression(minorThan);
    }

    @Override
    public void visit(MinorThanEquals minorThanEquals){
        handleComparisonExpression(minorThanEquals);
    }

    @Override
    public void visit(ExistsExpression existsExpression) {
        // tr_f(not exists(E)) = tr_f(empty(E)) := ¬empty(toSQL(E)) = exists(toSQL(E))
        if (existsExpression.isNot()){
            existsExpression.setNot(false);
            this.setParentNode(existsExpression, ChildPosition.RIGHT);
            this.trTExpressionVisitor.setParentNode(this.getParentNode());
            existsExpression.getRightExpression().accept(this.trTExpressionVisitor);
        }
        // tr_f(exists(E)) = tr_f(¬empty(E)) := tr_t(empty(E)) = tr_t(¬exists(E)
        else {
            existsExpression.setNot(true);
            setASTNode(existsExpression);
            this.trTExpressionVisitor.setParentNode(this.getParentNode());
            existsExpression.accept(this.trTExpressionVisitor);
        }
    }

    @Override
    public void visit(InExpression inExpression) {
        // tr_f(t NOT IN (E)) := tr_t(t IN (E))
        if (inExpression.isNot()) {
            inExpression.setNot(false);
            this.trTExpressionVisitor.setParentNode(this.getParentNode());
            inExpression.accept(this.trTExpressionVisitor);
        }
        else {
            // tr_f(t IN (E)) :=  tr_f(t = ANY(E))
            if (inExpression.getRightExpression() instanceof Select) {
                Expression newEqualsExpression = new EqualsTo(inExpression.getLeftExpression(),
                        new AnyComparisonExpression(AnyType.ANY, (Select) inExpression.getRightExpression()));
                setASTNode(newEqualsExpression);
                newEqualsExpression.accept(this);
            }
            else {
                inExpression.setNot(true);
                setParentNode(inExpression, ChildPosition.LEFT);
                inExpression.getLeftExpression().accept(this);
                setParentNode(inExpression, ChildPosition.RIGHT);
                inExpression.getRightExpression().accept(this);
            }
        }
    }

    @Override
    public void visit(OrExpression orExpression){
        // tr_f(θ_1 OR θ_2) := tr_f(θ_1) AND tr_f(θ_2)
        AndExpression andExpression = new AndExpression(orExpression.getLeftExpression(), orExpression.getRightExpression());
        setASTNode(andExpression);
        visitBinaryExpression(andExpression);
    }

    @Override
    public void visit(AndExpression andExpression){
        // tr_f(θ_1 AND θ_2) := tr_f(θ_1) OR tr_f(θ_2)
        OrExpression orExpression = new OrExpression(andExpression.getLeftExpression(), andExpression.getRightExpression());
        setASTNode(orExpression);
        visitBinaryExpression(orExpression);
    }

    @Override
    public void visit(NotEqualsTo notEqualsTo) {
        // tr_f(¬θ) = tr_t(θ)
        EqualsTo equalsTo = createEqualsFromNotEquals(notEqualsTo);
        setASTNode(equalsTo);
        this.trTExpressionVisitor.setParentNode(this.getParentNode());
        equalsTo.accept(this.trTExpressionVisitor);
    }

    @Override
    public void visit(NotExpression notExpression) {
        // tr_f(¬θ) = tr_t(θ)
        setASTNode(notExpression.getExpression());
        this.trTExpressionVisitor.setParentNode(this.getParentNode());
        notExpression.getExpression().accept(this.trTExpressionVisitor);
    }

    /***
     * handle comparison expression, such as =, >, <, >=, <=
     * @param comparisonExpression
     */
    private void handleComparisonExpression(ComparisonOperator comparisonExpression){
        // if the expression is t ω t', and t and t' are terms
        if (isBasicExpression(comparisonExpression)) {
            handleBasicComparison(comparisonExpression);
        }
        // if the expression is t ω ANY(E)
        else if (isAnyComparisonExpression(comparisonExpression)){
            handleAnyComparison(comparisonExpression);
        }
        // if the expression is t ω ALL(E)
        else if (isAllComparisonExpression(comparisonExpression)){
            handleAllComparison(comparisonExpression);
        }
        // if the expression is (t1, t2, ...) ω (t'1, t'2, ...)
        else if (isParenthesedExpressionListComparison(comparisonExpression)){
            handleParenthesedExpressionList(comparisonExpression);
        }

        else {
            this.setASTNode(new NotExpression(new Parenthesis(comparisonExpression)));
            this.trTExpressionVisitor.visitBinaryExpression(comparisonExpression);}
    }

    /***
     * translate t ω ALL(E) to ¬empty(σ_θ(toSQL(E))) := exists(σ_θ(toSQL(E))) = exists(SELECT * FROM (toSQL(E)) AS
     * SUB_QUERY_NAME WHERE θ)
     * @param expression
     */
    private void handleAllComparison(ComparisonOperator expression) {
        // if the query is of the shape ALL(..) ω t, replace the expressions, i.e. t ω ALL(...)
        ComparisonOperator fixedExpression = flipExpression(expression);
        String operator = expression.getStringExpression();

        // add exists(...)
        Select allSelect = ((AnyComparisonExpression) fixedExpression.getRightExpression()).getSelect();
        ExistsExpression existsExpr = new ExistsExpression();
        existsExpr.setRightExpression(allSelect);
        setASTNode(existsExpr);

        // add (SELECT * FROM (toSQL(E))) AS SUB_QUERY_NAME
        addSubQuery(allSelect, fixedExpression.getLeftExpression(), operator, existsExpr, "ALL");
    }

    /***
     * translate t ω ANY(E) to empty(σ_¬θ(toSQL(E))) = not exists (σ_¬θ(toSQL(E))) = not exists(SELECT * FROM (toSQL(E))
     * AS SUB_QUERY_NAME WHERE ¬θ)
     * @param expression
     */
    private void handleAnyComparison(ComparisonOperator expression) {
        // if the query is of the shape ALL(..) ω t, replace the expressions, i.e. t ω ALL(...)
        ComparisonOperator fixedExpression = flipExpression(expression);
        String operator = expression.getStringExpression();

        // add not exists(...)
        Select anySelect = ((AnyComparisonExpression) fixedExpression.getRightExpression()).getSelect();
        ExistsExpression notExistsExpr = new ExistsExpression();
        notExistsExpr.setRightExpression(anySelect);
        notExistsExpr.setNot(true);
        setASTNode(notExistsExpr);

        // add (SELECT * FROM (toSQL(E))) AS SUB_QUERY_NAME WHERE ¬(tr_f(t ω l(e)))
        addSubQuery(anySelect, fixedExpression.getLeftExpression(), operator, notExistsExpr, "ANY");
    }

    /***
     * add to the AST a sub query - (SELECT * FROM (toSQL(E))) AS SUB_QUERY_NAME WHERE (tr_f(t ω l(e))) for ALL expression
     * and ¬(tr_f(t ω l(e))) if Any expression
     * @param select
     * @param leftExpression
     * @param operator
     * @param anyType
     */
    private void addSubQuery(Select select, Expression leftExpression, String operator, ExistsExpression existsExpression,
                             String anyType) {
        // run toSQL(E)
        setParentNode(existsExpression, ChildPosition.RIGHT);
        select.accept(this.trTExpressionVisitor.getVisitorManager().getSelectVisitor());

        // add (SELECT * FROM (toSQL(E))) AS SUB_QUERY_NAME WHERE...
        setParentNode(existsExpression, ChildPosition.RIGHT);
        ParenthesedSelect addedSubQuery = (ParenthesedSelect) Parser.parseStringToAst("(SELECT * FROM" + select.toString() + "AS " + SUB_QUERY_NAME +")");
        AndExpression whereDummyRoot = new AndExpression();
        addedSubQuery.getPlainSelect().setWhere(whereDummyRoot);
        setASTNode(addedSubQuery);

        // add -  WHERE θ := WHERE (tr_f(t ω l(e)))
        setParentNode(whereDummyRoot, ChildPosition.RIGHT);
        Expression tethaRightExpression;
        ParenthesedExpressionList parenthesedSelectItems = getParenthesedExpressionList(select);
        if (leftExpression instanceof ParenthesedExpressionList) tethaRightExpression = parenthesedSelectItems;
        else tethaRightExpression = new Column(new Table(SUB_QUERY_NAME), ((Column) parenthesedSelectItems.get(0)).getColumnName());
        Expression tetha = createComparisonExpression(leftExpression, tethaRightExpression, operator);
        // calculate tr_f(t ω l(e))
        whereDummyRoot.setRightExpression(tetha);
        tetha.accept(this);
        tetha = whereDummyRoot.getRightExpression();
        // add ¬θ if ANY OR θ if ALL
        Expression notTetha = tetha instanceof Parenthesis ? new NotExpression(tetha) : new NotExpression(new Parenthesis(tetha));
        addedSubQuery.getPlainSelect().setWhere(anyType.equals("ANY") ? notTetha : tetha);
    }

    /***
     * translate t ω t' to t IS NULL or t' IS NULL or ¬ t ω t'
     * @param expression
     */
    private void handleBasicComparison(ComparisonOperator expression) {
        // add t IS NULL and t' IS NULL
        IsNullExpression leftIsNullExpr = ExpressionUtils.createIsNULExpression(expression.getLeftExpression());
        IsNullExpression rightIsNullExpr = ExpressionUtils.createIsNULExpression(expression.getRightExpression());
        OrExpression combinedExpression = new OrExpression();
        if (ifNotAddNullCondition(expression.getLeftExpression())) {
            combinedExpression.setLeftExpression(rightIsNullExpr);
        } else if (ifNotAddNullCondition(expression.getRightExpression())){
            combinedExpression.setLeftExpression(leftIsNullExpr);
        } else{
            combinedExpression.setLeftExpression(new OrExpression(leftIsNullExpr, rightIsNullExpr));
        }

        // add ¬ t ω t'
        combinedExpression.setRightExpression(new NotExpression(expression));
        setASTNode(new Parenthesis(combinedExpression));
    }

    /***
     * gets a comparison expression with parenthesed expression list, i.e. (t1, t2,...) ω (t'1, t'2, ...) and translate
     * it to tr_f(t1 ω t'1) and tr_f(t2 ω t'2) ...
     * @param expression
     */
    private void handleParenthesedExpressionList(ComparisonOperator expression){
        ParenthesedExpressionList leftExpression = (ParenthesedExpressionList) expression.getLeftExpression();
        ParenthesedExpressionList rightExpression = (ParenthesedExpressionList) expression.getRightExpression();
        // change to t1 ω t'1
        Expression newWhere = createComparisonExpression((Expression) leftExpression.get(0), (Expression) rightExpression.get(0), expression.getStringExpression());

        int index = 1;
        // if there is more than one item changes them also
        while (index < leftExpression.size()){
            Expression additionalCondition = createComparisonExpression((Expression) leftExpression.get(index), (Expression) rightExpression.get(index), expression.getStringExpression());
            newWhere = new OrExpression(newWhere, additionalCondition);
            index ++;
        }

        // calculate tr_f(t1 ω t'1) AND tr_f(t2...) ...
        setASTNode(newWhere);
        newWhere.accept(this);
    }

    /***
     * Gets an expression and checks if we should not add to the query - 'expression' IS NULL. If the expression is not a
     * term, or is "NULL" or a value, we don't need to add it
     * @param expression
     * @return
     */
    private boolean ifNotAddNullCondition(Expression expression){
        if (isTerm(expression)) {
            if (expression instanceof Column && (Objects.equals(((Column) expression).getColumnName(), "ANY") ||
                    Objects.equals(((Column) expression).getColumnName(), "NULL") ||
                    Objects.equals(((Column) expression).getColumnName(), "ALL")))return true;
            return isValue(expression) || isFunction(expression);
        }
        return true;
    }

    /***
     * Gets a select statement and returns a list of the select items
     * @param select
     * @return
     */
    private List<SelectItem<?>> getSelectItemsList(Select select){
        if (select instanceof ParenthesedSelect) return getSelectItemsList(((ParenthesedSelect) select).getSelect());
        else if (select instanceof PlainSelect){
            if (select.getPlainSelect().getSelectItems().get(0).getExpression() instanceof AllColumns){
                // case: SELECT *
            }
            else {
                return select.getPlainSelect().getSelectItems();
            }
        }
        else if (select instanceof SetOperationList) return getSelectItemsList(((SetOperationList) select).getSelects().get(0));

        return null;
    }

    /***
     * Gets a select statement and returns the select items as a ParenthesedExpressionList
     * @param select
     * @return
     */
    private ParenthesedExpressionList getParenthesedExpressionList(Select select) {
        List<SelectItem<?>> selectItemsList = getSelectItemsList(select);
        ParenthesedExpressionList parenthesedSelectItems = new ParenthesedExpressionList();
        for (SelectItem item : selectItemsList) {
            if (item.getExpression() instanceof Column && item.getAlias() == null) {
                parenthesedSelectItems.add(new Column(new Table(SUB_QUERY_NAME), ((Column) item.getExpression()).getColumnName()));
            }
            else {
                parenthesedSelectItems.add(new Column(new Table(SUB_QUERY_NAME), item.getAlias().getName()));
            }
        }
        return parenthesedSelectItems;
    }

    /***
     * Gets an comparison operator, with ANY or ALL, and flip the comparison expression if needed
     * @param expression
     * @return
     */
    private ComparisonOperator flipExpression(ComparisonOperator expression) {
        Expression leftExpression = getExpressionWithoutParenthesis(expression.getLeftExpression());
        Expression rightExpression = getExpressionWithoutParenthesis(expression.getRightExpression());
        if (leftExpression instanceof AnyComparisonExpression){
            leftExpression = rightExpression;
            rightExpression = getExpressionWithoutParenthesis(expression.getLeftExpression());
        }
        return createComparisonExpression(leftExpression, rightExpression, expression.getStringExpression());
    }
}

