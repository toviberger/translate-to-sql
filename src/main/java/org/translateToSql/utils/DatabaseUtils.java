package org.translateToSql.utils;

import org.translateToSql.model.DatabaseMetadata;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatabaseUtils {

    /***
     * Gets a string that represents a database metadata and converts it to a DatabaseMetadata object
     * @param dbString "Table1(col1, col2,...) \n Table2(col1, col2,...) .... "
     */
    public static DatabaseMetadata parseDbStringToDbObject(String dbString){
        DatabaseMetadata dbObject = new DatabaseMetadata();
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

        return dbObject;
    }
}
