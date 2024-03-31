package org.translateToSql.visitors.validationVisitors;

import net.sf.jsqlparser.expression.AnyComparisonExpression;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.schema.Column;
import org.translateToSql.model.Database;

public class ValidationExpressionVisitor extends ExpressionVisitorAdapter {
    private final ValidationSelectVisitor selectVisitor;
    private Database db;

    public ValidationExpressionVisitor(Database db, ValidationSelectVisitor selectVisitor){
        this.db = db;
        this.selectVisitor = selectVisitor;
    }

    public void visit(AnyComparisonExpression expr) {
        expr.getSelect().accept(selectVisitor);
    }

    public void visit(Column column){
        if (!db.ifTableExists(column.getTable().getName())) {
            throw new RuntimeException("An error occurred: illegal query - table doesn't exist");
        }
        if (!db.getTables().get(column.getTable().getName()).contains(column.getColumnName())){
            throw new RuntimeException("An error occurred: illegal query - column doesn't exist");
        }
    }
}
