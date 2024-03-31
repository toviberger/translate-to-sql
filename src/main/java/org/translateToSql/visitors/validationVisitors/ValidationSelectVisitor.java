package org.translateToSql.visitors.validationVisitors;

import net.sf.jsqlparser.statement.select.*;
import org.translateToSql.model.Database;

public class ValidationSelectVisitor extends SelectVisitorAdapter {

    private final ValidationExpressionVisitor expressionVisitor;
    private final ValidationFromItemVisitor fromItemVisitor;
    private final ValidationSelectItemVisitor selectItemVisitor;
    private final ValidationGroupByVisitor groupByVisitor;
    private Database db;

    public ValidationSelectVisitor(Database db){
        this.db = db;
        this.expressionVisitor = new ValidationExpressionVisitor(db, this);
        this.groupByVisitor = new ValidationGroupByVisitor(this.expressionVisitor);
        this.selectItemVisitor = new ValidationSelectItemVisitor(this.expressionVisitor);
        this.fromItemVisitor = new ValidationFromItemVisitor(db, this);
    }


    @Override
    public void visit(ParenthesedSelect parenthesedSelect) {
        parenthesedSelect.getSelect().accept(this);
    }

    @Override
    public void visit(PlainSelect plainSelect) {
        plainSelect.getSelectItems().forEach(selectItem -> selectItem.accept(this.selectItemVisitor));

        // visit from items
        plainSelect.getFromItem().accept(this.fromItemVisitor);

        // visit where conditions
        if (plainSelect.getWhere() != null) {
            plainSelect.getWhere().accept(this.expressionVisitor);
        }

        // visit group by
        if (plainSelect.getGroupBy() != null) {
            plainSelect.getGroupBy().accept(groupByElement -> groupByElement.accept(groupByVisitor));
        }

        // visit having
        if (plainSelect.getHaving() != null) {
            plainSelect.getHaving().accept(this.expressionVisitor);
        }
    }

    @Override
    public void visit(SetOperationList setOpList)
    {
        setOpList.getSelects().forEach(select -> select.accept(this));
    }
}
