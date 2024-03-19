package org.translateToSql;

public interface ToSqlVisitor {

    VisitorManager getVisitorManager();
    void setVisitorManager(VisitorManager visitorManager);

}
