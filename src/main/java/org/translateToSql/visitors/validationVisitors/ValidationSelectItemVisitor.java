package org.translateToSql.visitors.validationVisitors;

import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitorAdapter;

public class ValidationSelectItemVisitor extends SelectItemVisitorAdapter {

    private final ValidationExpressionVisitor expressionVisitor;

    public ValidationSelectItemVisitor(ValidationExpressionVisitor expressionVisitor){
        this.expressionVisitor = expressionVisitor;
    }

    @Override
    public void visit(SelectItem selectItem) {
        selectItem.getExpression().accept(expressionVisitor);
    }
}
