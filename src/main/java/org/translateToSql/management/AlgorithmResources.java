package org.translateToSql.management;

import org.translateToSql.model.DatabaseMetadata;

/***
 *  Bundles together resources essential for the translation algorithm, including VisitorManager and DatabaseMetaData.
 *  This encapsulation facilitates easy access to shared resources across different visitors.
 */
public class AlgorithmResources {

    private VisitorManager visitorManager;
    private DatabaseMetadata db;

    public AlgorithmResources(VisitorManager visitorManager, DatabaseMetadata db){
        this.visitorManager = visitorManager;
        this.db = db;
    }

    public VisitorManager getVisitorManager() {
        return visitorManager;
    }

    public void setVisitorManager(VisitorManager visitorManager) {
        this.visitorManager = visitorManager;
    }

    public DatabaseMetadata getDb() {
        return db;
    }

    public void setDb(DatabaseMetadata db) {
        this.db = db;
    }
}
