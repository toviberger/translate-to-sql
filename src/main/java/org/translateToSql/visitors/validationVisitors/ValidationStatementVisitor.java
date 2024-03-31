package org.translateToSql.visitors.validationVisitors;

import net.sf.jsqlparser.statement.StatementVisitorAdapter;
import net.sf.jsqlparser.statement.select.Select;
import org.translateToSql.model.Database;

public class ValidationStatementVisitor extends StatementVisitorAdapter {

    private final ValidationSelectVisitor selectVisitor;
    private Database db;

    public ValidationStatementVisitor(Database db){
        this.db = db;
        this.selectVisitor = new ValidationSelectVisitor(db);
    }

    @Override
    public void visit(Select select) {
        select.accept(selectVisitor);
    }
}
