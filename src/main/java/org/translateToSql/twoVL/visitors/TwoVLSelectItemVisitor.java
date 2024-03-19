package org.translateToSql.twoVL.visitors;

import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.SelectItem;
import org.translateToSql.ToSqlSelectItemVisitor;
import org.translateToSql.VisitorManager;

public class TwoVLSelectItemVisitor implements ToSqlSelectItemVisitor {

    private VisitorManager visitorManager;


    @Override
    public void visit(SelectItem selectItem) {
        // a dummy root for the expression AST
        AndExpression dummyRoot = new AndExpression(new Column("TRUE"), selectItem.getExpression());
        selectItem.setExpression(dummyRoot);
        selectItem.getExpression().accept(this.getVisitorManager().getExpressionVisitor());
        selectItem.setExpression(dummyRoot.getRightExpression());
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
