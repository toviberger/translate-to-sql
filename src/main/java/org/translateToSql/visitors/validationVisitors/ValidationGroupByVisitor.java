package org.translateToSql.visitors.validationVisitors;

import net.sf.jsqlparser.statement.select.GroupByElement;
import net.sf.jsqlparser.statement.select.GroupByVisitor;

public class ValidationGroupByVisitor implements GroupByVisitor {

    private final ValidationExpressionVisitor expressionVisitor;

    public ValidationGroupByVisitor(ValidationExpressionVisitor expressionVisitor){
        this.expressionVisitor = expressionVisitor;
    }

    @Override
    public void visit(GroupByElement groupByElement) {
        groupByElement.getGroupByExpressionList().accept(expressionVisitor);
    }
}
