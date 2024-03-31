package org.translateToSql.model;

import java.util.*;

/***
 * A class for representing a Database. It has a Map that contains a table name and its column names.
 */
public class Database {

    private Map<String, List<String>> tables;

    public Database(){
        this.tables = new HashMap<>();
    }

    public Database(Map<String, List<String>> tables){
        this.tables = tables;
    }

    public Map<String, List<String>> getTables() {
        return tables;
    }

    public void addTable(String tableName, List<String> columns){
        this.tables.put(tableName, columns);
    }

    public boolean ifTableExists(String tableName){
        return this.tables.containsKey(tableName);
    }
}
