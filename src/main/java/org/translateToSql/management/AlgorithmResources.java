package org.translateToSql.management;

import org.translateToSql.model.Schema;

/***
 *  Bundles together resources essential for the translation algorithm, including VisitorManager and Schema.
 *  This encapsulation facilitates easy access to shared resources across different visitors.
 */
public class AlgorithmResources {

    private VisitorManager visitorManager;
    private Schema schema;

    public AlgorithmResources(VisitorManager visitorManager, Schema schema){
        this.visitorManager = visitorManager;
        this.schema = schema;
    }

    public VisitorManager getVisitorManager() {
        return visitorManager;
    }

    public void setVisitorManager(VisitorManager visitorManager) {
        this.visitorManager = visitorManager;
    }

    public Schema getSchema() {
        return schema;
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }
}
