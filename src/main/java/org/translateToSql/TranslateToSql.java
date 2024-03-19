package org.translateToSql;

public abstract class TranslateToSql{
    private VisitorManager visitorManager;

    protected TranslateToSql(ToSqlExpressionVisitor expressionVisitor,
                             ToSqlSelectItemVisitor selectItemVisitor,
                             ToSqlSelectVisitor selectVisitor,
                             ToSqlFromItemVisitor fromItemVisitor,
                             ToSqlStatementVisitor statementVisitor) {
        this.visitorManager = new VisitorManager(expressionVisitor, selectItemVisitor, selectVisitor, fromItemVisitor, statementVisitor);
        this.visitorManager.getExpressionVisitor().setVisitorManager(this.visitorManager);
        this.visitorManager.getSelectVisitor().setVisitorManager(this.visitorManager);
        this.visitorManager.getSelectItemVisitor().setVisitorManager(this.visitorManager);
        this.visitorManager.getFromItemVisitor().setVisitorManager(this.visitorManager);
        this.visitorManager.getStatementVisitor().setVisitorManager(this.visitorManager);
    }

    public abstract String translate(String query);

    public VisitorManager getVisitorManager() {
        return visitorManager;
    }

    public void setVisitorManager(VisitorManager visitorManager) {
        this.visitorManager = visitorManager;
    }
}
