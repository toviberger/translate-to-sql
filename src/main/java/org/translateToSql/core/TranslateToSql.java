package org.translateToSql.core;

import net.sf.jsqlparser.statement.Statement;
import org.translateToSql.management.AlgorithmResources;
import org.translateToSql.management.VisitorManager;
import org.translateToSql.model.Database;
import org.translateToSql.visitors.translationVisitors.*;
import org.translateToSql.visitors.validationVisitors.ValidationStatementVisitor;

public abstract class TranslateToSql{
    private final AlgorithmResources algorithmResources;

    protected TranslateToSql(ToSqlExpressionVisitor expressionVisitor,
                             ToSqlSelectItemVisitor selectItemVisitor,
                             ToSqlSelectVisitor selectVisitor,
                             ToSqlFromItemVisitor fromItemVisitor,
                             ToSqlStatementVisitor statementVisitor,
                             Database db) {
        VisitorManager visitorManager = new VisitorManager(expressionVisitor, selectItemVisitor, selectVisitor, fromItemVisitor, statementVisitor);
        this.algorithmResources = new AlgorithmResources(visitorManager, db);
        this.algorithmResources.getVisitorManager().getExpressionVisitor().setAlgorithmResources(this.algorithmResources);
        this.algorithmResources.getVisitorManager().getSelectVisitor().setAlgorithmResources(this.algorithmResources);
        this.algorithmResources.getVisitorManager().getSelectItemVisitor().setAlgorithmResources(this.algorithmResources);
        this.algorithmResources.getVisitorManager().getFromItemVisitor().setAlgorithmResources(this.algorithmResources);
        this.algorithmResources.getVisitorManager().getStatementVisitor().setAlgorithmResources(this.algorithmResources);
    }

    public abstract String translate(String query);

    public VisitorManager getVisitorManager() {
        return this.algorithmResources.getVisitorManager();
    }

    public Database getDB(){
        return this.algorithmResources.getDb();
    }

    public void validate(Statement query){
        query.accept(new ValidationStatementVisitor(this.getDB()));
    }
}
