package org.translateToSql;

import org.translateToSql.database.Database;

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
}
