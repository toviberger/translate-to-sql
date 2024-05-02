package org.translateToSql.core;

import org.translateToSql.management.AlgorithmResources;
import org.translateToSql.management.VisitorManager;
import org.translateToSql.model.Schema;
import org.translateToSql.translationVisitors.*;

/***
 * An abstract base class that outlines the structure for translation classes. It initializes essential components such
 * as various specialized visitors (ToSqlExpressionVisitor, ToSqlSelectVisitor, etc.) and schema, which represent
 * the schema information of the target database.
 */
public abstract class TranslateToSql{
    private final AlgorithmResources algorithmResources;

    protected TranslateToSql(ToSqlExpressionVisitor expressionVisitor,
                             ToSqlSelectItemVisitor selectItemVisitor,
                             ToSqlSelectVisitor selectVisitor,
                             ToSqlFromItemVisitor fromItemVisitor,
                             ToSqlStatementVisitor statementVisitor,
                             Schema schema) {
        VisitorManager visitorManager = new VisitorManager(expressionVisitor, selectItemVisitor, selectVisitor, fromItemVisitor, statementVisitor);
        this.algorithmResources = new AlgorithmResources(visitorManager, schema);
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

    public Schema getSchema(){
        return this.algorithmResources.getSchema();
    }
}
