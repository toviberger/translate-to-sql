package org.translateToSql.visitors.validationVisitors;

import net.sf.jsqlparser.expression.AnyComparisonExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.schema.Column;
import org.translateToSql.model.Database;

public class ValidationExpressionVisitor extends ExpressionVisitorAdapter {
    private final ValidationSelectVisitor selectVisitor;
    private Database db;


    public ValidationExpressionVisitor(Database db, ValidationSelectVisitor selectVisitor){
        this.db = db;
        this.selectVisitor = selectVisitor;
    }

    @Override
    public void visit(AnyComparisonExpression expr) {
        expr.getSelect().accept(selectVisitor);
    }

    @Override
    public void visit(Function function){
        function.getParameters().forEach(expression -> expression.accept(this));
    }

    @Override
    public void visit(Column column){
        if (column.getTable() != null && !db.ifTableExists(column.getTable().getName())) {
            throw new RuntimeException("An error occurred: illegal query - table doesn't exist");
        }
        if (column.getTable() != null && !db.getTables().get(column.getTable().getName()).contains(column.getColumnName())){
            throw new RuntimeException("An error occurred: illegal query - column doesn't exist");
        }
    }
}
