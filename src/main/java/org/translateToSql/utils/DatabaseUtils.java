package org.translateToSql.utils;

import org.translateToSql.model.Database;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatabaseUtils {

    /***
     * Gets a string that represents a database and converts it to a database object
     * @param dbString "Table1(col1, col2,...) \n Table2(col1, col2,...) .... "
     */
    public static Database parseDbStringToDbObject(String dbString){
        Database dbObject = new Database();
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
