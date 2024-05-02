package org.translateToSql.utils;

import org.translateToSql.model.Schema;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SchemaUtils {

    /***
     * Gets a string that represents a schema and converts it to a Schema object
     * @param schemaString "Table1(col1, col2,...) \n Table2(col1, col2,...) .... "
     */
    public static Schema stringToSchema(String schemaString){
        Schema schema = new Schema();
        String[] tableDefinitions = schemaString.split("\\n+");

        // Pattern to match table name and columns (e.g., R(a,b))
        Pattern pattern = Pattern.compile("([A-Za-z]+)\\(([^)]+)\\)");

        for (String definition : tableDefinitions) {
            Matcher matcher = pattern.matcher(definition);
            if (matcher.find()) {
                String tableName = matcher.group(1);
                String[] columns = matcher.group(2).split(",");
                schema.addTable(tableName, new ArrayList<>());
                for (String column : columns) {
                    schema.getTables().get(tableName).add(column.trim());
                }
            }
        }

        return schema;
    }

    /***
     * Gets a Schema and converts it to a string that represents a schema
     * @param schema
     */
    public static String schemaToString(Schema schema) {
        StringBuilder result = new StringBuilder();

        // Iterate over the schema map entries
        for (Map.Entry<String, List<String>> entry : schema.getTables().entrySet()) {
            String tableName = entry.getKey();
            List<String> columns = entry.getValue();

            // Append table name and columns in the desired format
            result.append(tableName).append("(");
            result.append(String.join(",", columns));
            result.append(")").append(System.lineSeparator());
        }

        return result.toString(); // Convert StringBuilder to String and return
    }
}
