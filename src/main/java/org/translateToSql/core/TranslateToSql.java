package org.translateToSql.core;

import org.translateToSql.management.AlgorithmResources;
import org.translateToSql.management.VisitorManager;
import org.translateToSql.model.DatabaseMetadata;
import org.translateToSql.translationVisitors.*;

/***
 * An abstract base class that outlines the structure for translation classes. It initializes essential components such
 * as various specialized visitors (ToSqlExpressionVisitor, ToSqlSelectVisitor, etc.) and DatabaseMetaData, which represent
 * the schema information of the target database.
 */
public abstract class TranslateToSql{
    private final AlgorithmResources algorithmResources;

    protected TranslateToSql(ToSqlExpressionVisitor expressionVisitor,
                             ToSqlSelectItemVisitor selectItemVisitor,
                             ToSqlSelectVisitor selectVisitor,
                             ToSqlFromItemVisitor fromItemVisitor,
                             ToSqlStatementVisitor statementVisitor,
                             DatabaseMetadata db) {
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

    public DatabaseMetadata getDB(){
        return this.algorithmResources.getDb();
    }
}
