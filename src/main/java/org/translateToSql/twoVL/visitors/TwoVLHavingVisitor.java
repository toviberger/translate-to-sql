package org.translateToSql.twoVL.visitors;

import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.ParenthesedSelect;
import net.sf.jsqlparser.statement.select.Select;
import org.translateToSql.utils.ChildPosition;
import org.translateToSql.utils.ExpressionUtils;

import java.util.Map;

public class TwoVLHavingVisitor extends TwoVLExpressionVisitor  {

    private final Map<String, String> columnAliasMapping;
    public TwoVLHavingVisitor(Map<String, String> columnAliasMapping){
        this.columnAliasMapping = columnAliasMapping;
    }

    @Override
    public void visit(Column column){
        column.setColumnName(columnAliasMapping.get(column.getColumnName()));
        column.setTable(new Table(ExpressionUtils.SUB_QUERY_NAME));
    }
    @Override
    public void visit(ExistsExpression existsExpression) {
        existsExpression.getRightExpression().accept(this);
    }

    @Override
    public void visit(NotExpression notExpression) {
        notExpression.getExpression().accept(this);
    }
    @Override
    public void visit(Select select) {

    }
    @Override
    public void visit(Function function){
        setASTNode(new Column(new Table(ExpressionUtils.SUB_QUERY_NAME), columnAliasMapping.get(function.toString())));

    }

    @Override
    public void visit(Addition addition) {
        handleArithmeticExpression(addition);
    }

    @Override
    public void visit(Division division) {
        handleArithmeticExpression(division);
    }

    @Override
    public void visit(IntegerDivision integerDivision) {
        handleArithmeticExpression(integerDivision);
    }

    @Override
    public void visit(Multiplication multiplication) {
        handleArithmeticExpression(multiplication);
    }

    @Override
    public void visit(Subtraction subtraction) {
        handleArithmeticExpression(subtraction);
    }

    @Override
    public void visit(AndExpression andExpression) {
        visitBinaryExpression(andExpression);
    }

    @Override
    public void visit(OrExpression orExpression) {
        visitBinaryExpression(orExpression);
    }

    @Override
    public void visit(Between between) {
        setParentNode(between, ChildPosition.LEFT);
        between.getLeftExpression().accept(this);
    }

    @Override
    public void visit(EqualsTo equalsTo) {
        visitBinaryExpression(equalsTo);
    }

    @Override
    public void visit(GreaterThan greaterThan) {
        visitBinaryExpression(greaterThan);
    }

    @Override
    public void visit(GreaterThanEquals greaterThanEquals) {
        visitBinaryExpression(greaterThanEquals);
    }

    @Override
    public void visit(InExpression inExpression) {
        setParentNode(inExpression, ChildPosition.LEFT);
        inExpression.getLeftExpression().accept(this);
        setParentNode(inExpression, ChildPosition.RIGHT);
        inExpression.getRightExpression().accept(this);
    }

    @Override
    public void visit(IsNullExpression isNullExpression) {
        setParentNode(isNullExpression, ChildPosition.LEFT);
        isNullExpression.getLeftExpression().accept(this);
    }

    @Override
    public void visit(IsBooleanExpression isBooleanExpression) {
        setParentNode(isBooleanExpression, ChildPosition.LEFT);
        isBooleanExpression.getLeftExpression().accept(this);
    }

    @Override
    public void visit(LikeExpression likeExpression) {
        visitBinaryExpression(likeExpression);
    }

    @Override
    public void visit(MinorThan minorThan) {
        visitBinaryExpression(minorThan);
    }

    @Override
    public void visit(MinorThanEquals minorThanEquals) {
        visitBinaryExpression(minorThanEquals);
    }

    @Override
    public void visit(NotEqualsTo notEqualsTo) {
        visitBinaryExpression(notEqualsTo);
    }

    @Override
    public void visit(ParenthesedSelect parenthesedSelect) {

    }

    /***
     * Gets an expression and checks if we need to change it to its new alias
     * @param expression
     */
    private void handleArithmeticExpression(BinaryExpression expression){
        if (columnAliasMapping.containsKey(expression.toString()))
            setASTNode(new Column(new Table(ExpressionUtils.SUB_QUERY_NAME), columnAliasMapping.get(expression.toString())));
        else visitBinaryExpression(expression);
    }
}
