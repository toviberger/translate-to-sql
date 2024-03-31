package org.translateToSql.management;

import org.translateToSql.model.Database;

/***
 * The AlgorithmResources class serves as a central repository providing essential resources, including visitor
 * coordination and database access, necessary for the execution of the algorithms.
 */
public class AlgorithmResources {

    private VisitorManager visitorManager;
    private Database db;

    public AlgorithmResources(VisitorManager visitorManager, Database db){
        this.visitorManager = visitorManager;
        this.db = db;
    }

    public VisitorManager getVisitorManager() {
        return visitorManager;
    }

    public void setVisitorManager(VisitorManager visitorManager) {
        this.visitorManager = visitorManager;
    }

    public Database getDb() {
        return db;
    }

    public void setDb(Database db) {
        this.db = db;
    }
}
