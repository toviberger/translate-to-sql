package org.translateToSql.utils;

import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.*;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.parser.SimpleNode;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.PlainSelect;

/***
 * Class for holding helper methods for the Expression object
 */
public class ExpressionUtils {

    public static final String SUB_QUERY_NAME = "S";
    public static final String COL_NAME = "col";

    /***
     * Checks whether an expression is of the shape t ω t', when t and t' are terms, and ω is +, -, >, < ...
     * @param expression t ω t'
     * @return true is the expression is basic
     */
    public static boolean isBasicExpression(ComparisonOperator expression) {
        return isTerm(expression.getLeftExpression()) && isTerm(expression.getRightExpression());
    }

    /***
     * Checks whether an expression is a term := n | c | null | f(t1, t2,...) ...
     * @param expression
     * @return true is the expression is a term
     */
    public static boolean isTerm(Expression expression) {
        if (expression instanceof Parenthesis) return isTerm(((Parenthesis) expression).getExpression());
        return (isValue(expression) || isColumn(expression) || isFunction(expression) || isArithmeticFunction(expression));
    }

    /***
     * Checks whether an expression is an arithmetic function - +, -, *, % ...
     * @param expression
     * @return true is the expression is an arithmetic function
     */
    public static boolean isArithmeticFunction(Expression expression){
        return expression instanceof Addition ||
                expression instanceof Multiplication ||
                expression instanceof Subtraction ||
                expression instanceof Modulo ||
                expression instanceof IntegerDivision ||
                expression instanceof Division ||
                expression instanceof Concat ||
                expression instanceof BitwiseAnd ||
                expression instanceof BitwiseLeftShift ||
                expression instanceof BitwiseOr ||
                expression instanceof BitwiseRightShift;
    }

    /***
     * Checks whether an expression is of the shape t, and t is a value
     * @param expression
     * @return true if the expression is a value
     */
    public static boolean isValue(Expression expression) {
        // Check if the expression is a basic value (literal)
        return expression instanceof LongValue ||
                expression instanceof DoubleValue ||
                expression instanceof StringValue ||
                expression instanceof DateValue ||
                expression instanceof TimeValue ||
                expression instanceof TimestampValue ||
                expression instanceof NullValue;
    }

    /***
     * Checks whether an expression is of the shape t, and t is a column
     * @param expression
     * @return true if the expression is a column
     */
    public static boolean isColumn(Expression expression) {
        // Check if the expression is a column reference
        return expression instanceof Column;
    }

    /***
     * Checks whether an expression is a parenthesed expression list comparison - (t1, t2,...) ω (t'1, t'2,...)
     * @param expression t ω t'
     * @return true if the  expression is a parenthesed expression list comparison
     */
    public static boolean isParenthesedExpressionListComparison(ComparisonOperator expression) {
        return isParenthesedExpressionList(expression.getLeftExpression()) ||
                isParenthesedExpressionList(expression.getRightExpression());
    }

    /***
     * Checks whether an expression is a parenthesed expression list - (t1, t2,...)
     * @param expression
     * @return true if the expression is a parenthesed expression list - (t1, t2,...)
     */
    public static boolean isParenthesedExpressionList(Expression expression){
        if (expression instanceof Parenthesis)
            return isParenthesedExpressionList(((Parenthesis) expression).getExpression());
        return expression instanceof ParenthesedExpressionList;
    }

    /***
     * Checks whether an expression is a function - sum, avg, ...
     * @param expression
     * @return true if the expression is a function
     */
    public static boolean isFunction(Expression expression){
        return expression instanceof Function;
    }

    /***
     * Checks whether an expression is of the shape t ω ANY(E)
     * @param expression
     * @return true if the expression is t ω ANY(E)
     */
    public static boolean isAnyComparisonExpression(BinaryExpression expression) {
        return  isAnyExpression(expression.getLeftExpression()) || isAnyExpression(expression.getRightExpression());
    }

    /***
     * Checks whether an expression is of the shape t ω ALL(E)
     * @param expression
     * @return true if the expression is t ω ALL(E)
     */
    public static boolean isAllComparisonExpression(BinaryExpression expression) {
        return  isAllExpression(expression.getLeftExpression()) || isAllExpression(expression.getRightExpression());
    }

    /***
     * Checks whether an expression is of the shape ANY(E)
     * @param expression
     * @return true if the expression is ANY(E)
     */
    private static boolean isAnyExpression(Expression expression){
        if (expression instanceof Parenthesis) return isAnyExpression(((Parenthesis) expression).getExpression());
        return expression instanceof AnyComparisonExpression && (
                (AnyComparisonExpression) expression).getAnyType() == AnyType.ANY;
    }

    /***
     * Checks whether an expression is of the shape ALL(E)
     * @param expression
     * @return true if the expression is ALL(E)
     */
    private static boolean isAllExpression(Expression expression){
        if (expression instanceof Parenthesis) return isAllExpression(((Parenthesis) expression).getExpression());
        return expression instanceof AnyComparisonExpression && (
                (AnyComparisonExpression) expression).getAnyType() == AnyType.ALL;
    }

    /***
     * Gets a NotEquals expression and returns the same expression but with =
     * @param notEqualsTo t != t'
     * @return an equals expression
     */
    public static EqualsTo createEqualsFromNotEquals(NotEqualsTo notEqualsTo) {
        EqualsTo equalsTo = new EqualsTo();
        equalsTo.setLeftExpression(notEqualsTo.getLeftExpression());
        equalsTo.setRightExpression(notEqualsTo.getRightExpression());
        equalsTo.setASTNode(notEqualsTo.getASTNode());
        return equalsTo;
    }

    /***
     * Gets 2 expressions and 1 operator and returns the appropriate expression
     * @param leftExpression
     * @param rightExpression
     * @param operator := =, >, < >=, <=
     * @return a comparison expression
     */
    public static ComparisonOperator createComparisonExpression(Expression leftExpression, Expression rightExpression, String operator){
        switch (operator){
            case "=": return new EqualsTo(leftExpression, rightExpression);
            case ">=": {
                GreaterThanEquals returnedExpression = new GreaterThanEquals();
                returnedExpression.setLeftExpression(leftExpression);
                returnedExpression.setRightExpression(rightExpression);
                return returnedExpression;
            }
            case "<=": {
                MinorThanEquals returnedExpression = new MinorThanEquals();
                returnedExpression.setLeftExpression(leftExpression);
                returnedExpression.setRightExpression(rightExpression);
                return returnedExpression;
            }
            case ">": {
                GreaterThan returnedExpression = new GreaterThan();
                returnedExpression.setLeftExpression(leftExpression);
                returnedExpression.setRightExpression(rightExpression);
                return returnedExpression;
            }
            case "<": {
                MinorThan returnedExpression = new MinorThan();
                returnedExpression.setLeftExpression(leftExpression);
                returnedExpression.setRightExpression(rightExpression);
                return returnedExpression;
            }
        }

        return null;
    }

    /***
     * Gets an expression and creates an 'IS NULL' expression
     * @param expression
     * @return 'expression' IS NULL
     */
    public static IsNullExpression createIsNULExpression(Expression expression){
        IsNullExpression isNullExpr = new IsNullExpression();
        isNullExpr.setLeftExpression(expression);
        return isNullExpr;
    }

    /***
     * gets an expression and if there are parenthesis, returns the inside expression
     * @param expression
     * @return an expression without parenthesis
     */
    public static Expression getExpressionWithoutParenthesis(Expression expression){
        if (expression instanceof Parenthesis)
            return getExpressionWithoutParenthesis(((Parenthesis) expression).getExpression());
        return expression;
    }

    /***
     * gets an expression and returns its table name
     * @param expression
     * @return table name / alias
     */
    public static String getTableName(Expression expression){
        SimpleNode node;
        if (expression instanceof BinaryExpression) node = ((BinaryExpression) expression).getLeftExpression().getASTNode();
        else node = expression.getASTNode();

        // calculate the root of the AST, to get the Select
        while (node.jjtGetParent() != null){
            node = (SimpleNode) node.jjtGetParent();
        }

        FromItem fromItem = ((PlainSelect) ((SimpleNode)node.jjtGetChild(0)).jjtGetValue()).getFromItem();
        if (fromItem.getAlias() != null)
            return fromItem.getAlias().getName();
        else
            return ((Table) fromItem).getName().toString();
    }

    /***
     * get an expression ant its table name and if it is a column it sets the table name
     * @param expression
     * @param table table name
     */
    public static void setTableName(Expression expression, String table){
        if (expression instanceof BinaryExpression){
            setTableName(((BinaryExpression) expression).getLeftExpression(), table);
            setTableName(((BinaryExpression) expression).getRightExpression(), table);
        }

        if (expression instanceof Column){
            ((Column) expression).setTable(new Table(table));
        }
    }
}
