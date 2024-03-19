package org.translateToSql.twoVL.visitors;

import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.*;
import org.translateToSql.ToSqlSelectVisitor;
import org.translateToSql.VisitorManager;

public class TwoVLSelectVisitor implements ToSqlSelectVisitor {

    private VisitorManager visitorManager;


    @Override
    public void visit(ParenthesedSelect parenthesedSelect) {
        parenthesedSelect.getSelect().accept(this);
    }

    @Override
    public void visit(PlainSelect plainSelect) {
        // visit select items
        plainSelect.getSelectItems().forEach(selectItem -> selectItem.accept(this.getVisitorManager().getSelectItemVisitor()));

        // visit from items
        plainSelect.getFromItem().accept(this.getVisitorManager().getFromItemVisitor());

        // visit where conditions
        if (plainSelect.getWhere() != null) {
            // dummy root for the where AST
            AndExpression dummyRoot = new AndExpression(new Column("TRUE"), plainSelect.getWhere());
            plainSelect.setWhere(dummyRoot);
            plainSelect.getWhere().accept(this.getVisitorManager().getExpressionVisitor());
            plainSelect.setWhere(dummyRoot.getRightExpression());
        }
    }

    @Override
    public void visit(SetOperationList setOpList)
    {
        setOpList.getSelects().forEach(select -> select.accept(this));
    }

    @Override
    public void visit(WithItem withItem) {

    }

    @Override
    public void visit(Values aThis) {

    }

    @Override
    public void visit(LateralSubSelect lateralSubSelect) {

    }

    @Override
    public void visit(TableStatement tableStatement) {

    }

    @Override
    public VisitorManager getVisitorManager() {
        return this.visitorManager;
    }

    @Override
    public void setVisitorManager(VisitorManager visitorManager) {
        this.visitorManager = visitorManager;
    }
}
