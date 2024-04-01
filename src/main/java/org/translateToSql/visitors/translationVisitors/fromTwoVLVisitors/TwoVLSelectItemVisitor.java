package org.translateToSql.visitors.translationVisitors.fromTwoVLVisitors;

import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.SelectItem;
import org.translateToSql.visitors.translationVisitors.ToSqlSelectItemVisitor;
import org.translateToSql.management.VisitorManager;
import org.translateToSql.management.AlgorithmResources;

public class TwoVLSelectItemVisitor implements ToSqlSelectItemVisitor {

    private AlgorithmResources algorithmResources;

    @Override
    public void visit(SelectItem selectItem) {
        // a dummy root for the expression AST
        AndExpression dummyRoot = new AndExpression(new Column("TRUE"), selectItem.getExpression());
        selectItem.setExpression(dummyRoot);
        selectItem.getExpression().accept(this.getVisitorManager().getExpressionVisitor());
        selectItem.setExpression(dummyRoot.getRightExpression());
    }

    public VisitorManager getVisitorManager() {
        return this.algorithmResources.getVisitorManager();
    }

    @Override
    public void setAlgorithmResources(AlgorithmResources algorithmResources) {
        this.algorithmResources = algorithmResources;
    }

    @Override
    public AlgorithmResources getAlgorithmResources(){
        return this.algorithmResources;
    }
}
