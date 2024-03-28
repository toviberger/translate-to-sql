package org.translateToSql.database;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatabaseUtils {

    /***
     * Gets a string that represents a database and converts it to a database object
     * @param dbString
     * @param dbObject
     */
    public static void parseDbStringToDbObject(String dbString, Database dbObject){
        String[] tableDefinitions = dbString.split("\\n+");

        // Pattern to match table name and columns (e.g., R(a,b))
        Pattern pattern = Pattern.compile("([A-Za-z]+)\\(([^)]+)\\)");

        for (String definition : tableDefinitions) {
            Matcher matcher = pattern.matcher(definition);
            if (matcher.find()) {
                String tableName = matcher.group(1);
                String[] columns = matcher.group(2).split(",");
                dbObject.addTable(tableName, new ArrayList<>());
                for (String column : columns) {
                    dbObject.getTables().get(tableName).add(column.trim());
                }
            }
        }
    }
}
