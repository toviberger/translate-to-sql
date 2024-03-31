package org.translateToSql.visitors.validationVisitors;

import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;
import org.translateToSql.model.Database;

public class ValidationFromItemVisitor extends FromItemVisitorAdapter {

    private final ValidationSelectVisitor selectVisitor;
    private Database db;

    public ValidationFromItemVisitor(Database db, ValidationSelectVisitor selectVisitor){
        this.db = db;
        this.selectVisitor = selectVisitor;
    }

    @Override
    public void visit(Table tableName) {
        if (!db.ifTableExists(tableName.getName())){
            throw new RuntimeException("An error occurred: illegal query - table doesn't exist");
        }
    }

    @Override
    public void visit(ParenthesedSelect selectBody) {
        selectBody.getPlainSelect().accept(selectVisitor);
    }

    @Override
    public void visit(ParenthesedFromItem aThis) {
        aThis.getFromItem().accept(this);
    }
}
