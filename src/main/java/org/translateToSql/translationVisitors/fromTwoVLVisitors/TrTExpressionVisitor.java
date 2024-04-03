package org.translateToSql.translationVisitors.fromTwoVLVisitors;

import net.sf.jsqlparser.expression.AnyComparisonExpression;
import net.sf.jsqlparser.expression.AnyType;
import net.sf.jsqlparser.expression.NotExpression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.statement.select.Select;
import org.translateToSql.model.ChildPosition;

import static org.translateToSql.utils.ExpressionUtils.createEqualsFromNotEquals;

public class TrTExpressionVisitor extends TwoVLExpressionVisitor {

    private final TrFExpressionVisitor trFVisitor = new TrFExpressionVisitor(this);

    @Override
    public void visit(IsNullExpression isNullExpression) {
        // tr_t(¬θ) = tr_f(θ)
        if (isNullExpression.isNot()){
            isNullExpression.setNot(false);
            this.trFVisitor.setParentNode(this.getParentNode());
            isNullExpression.accept(this.getTrFVisitor());
        }

        else {
            this.setParentNode(isNullExpression, ChildPosition.LEFT);
            isNullExpression.getLeftExpression().accept(this);
        }
    }

    @Override
    public void visit(IsBooleanExpression isBooleanExpression) {
        // tr_t(¬θ) = tr_f(θ)
        if (isBooleanExpression.isNot()){
            isBooleanExpression.setNot(false);
            this.trFVisitor.setParentNode(this.getParentNode());
            isBooleanExpression.accept(this.getTrFVisitor());
        }

        else {
            this.setParentNode(isBooleanExpression, ChildPosition.LEFT);
            isBooleanExpression.getLeftExpression().accept(this);
        }
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
    public void visit(MinorThan minorThan) {
        visitBinaryExpression(minorThan);
    }

    @Override
    public void visit(MinorThanEquals minorThanEquals) {
        visitBinaryExpression(minorThanEquals);
    }

    @Override
    public void visit(InExpression inExpression) {
        // tr_t(t NOT IN (E)) := tr_f(t IN (E))
        if (inExpression.isNot()) {
            inExpression.setNot(false);
            this.trFVisitor.setParentNode(this.getParentNode());
            inExpression.accept(this.trFVisitor);
        }
        else {
            if (inExpression.getRightExpression() instanceof Select) {
                // t IN (E) :=  t = ANY(E)
                AnyComparisonExpression newAnyComparisonExpression = new AnyComparisonExpression(
                        AnyType.ANY, (Select) inExpression.getRightExpression());
                EqualsTo newEqualsExpression = new EqualsTo(inExpression.getLeftExpression(), newAnyComparisonExpression);
                setASTNode(newEqualsExpression);
                newEqualsExpression.accept(this);
            }
            else {
                // t IN (ARRAY...)
                setParentNode(inExpression, ChildPosition.LEFT);
                inExpression.getLeftExpression().accept(this);
                setParentNode(inExpression, ChildPosition.RIGHT);
                inExpression.getRightExpression().accept(this);
            }
        }
    }

    @Override
    public void visit(AnyComparisonExpression anyComparisonExpression) {
        // tr_t(ANY(E)) := ANY(toSQL(E)) || tr_t(ALL(E)) := ALL(toSQL(E))
        anyComparisonExpression.getSelect().accept(this.getVisitorManager().getStatementVisitor());
    }

    @Override
    public void visit(ExistsExpression existsExpression) {
        // tr_t(¬exists(E)) = tr_t(empty(E)) := empty(toSQL(E)) = ¬exists(toSQL(E))
        if (existsExpression.isNot()){
            this.setParentNode(existsExpression, ChildPosition.RIGHT);
            existsExpression.getRightExpression().accept(this);
        }
        else {
            // tr_t(exists(E)) = tr_t(¬empty(E)) = tr_f(empty(E) = tr_f(¬exists(E)
            existsExpression.setNot(true);
            this.trFVisitor.setParentNode(this.getParentNode());
            existsExpression.accept(this.getTrFVisitor());
        }
    }

    @Override
    public void visit(NotEqualsTo notEqualsTo) {
        // tr_t(¬θ) = tr_f(θ)
        EqualsTo equalsTo = createEqualsFromNotEquals(notEqualsTo);
        setASTNode(equalsTo);
        this.trFVisitor.setParentNode(this.getParentNode());
        equalsTo.accept(this.getTrFVisitor());
    }

    @Override
    public void visit(NotExpression aThis) {
        // tr_t(¬θ) = tr_f(θ)
        setASTNode(aThis.getExpression());
        this.trFVisitor.setParentNode(this.getParentNode());
        aThis.getExpression().accept(this.getTrFVisitor());
    }

    @Override
    public void visit(AndExpression andExpression) {
        visitBinaryExpression(andExpression);
    }

    @Override
    public void visit(OrExpression orExpression) {
        visitBinaryExpression(orExpression);
    }

    public TrFExpressionVisitor getTrFVisitor() {
        return trFVisitor;
    }
}
