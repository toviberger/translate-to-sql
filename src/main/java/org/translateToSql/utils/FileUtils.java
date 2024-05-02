package org.translateToSql.utils;

import org.translateToSql.core.TranslateToSql;
import org.translateToSql.model.Schema;
import org.translateToSql.model.InputFileData;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    /***
     * Gets a file path with schema and queries, parses it and returns InputFileData.
     * Ths file has the format -
     * 'Table1(col1,...)
     * Table2(col1,...)
     *
     * Select...
     * Select...
     * ...'
     *
     * @param filePath path to the file that stores the data
     * @return InputFileData
     */
    public static InputFileData parseFile(String filePath) {
        Schema schema = new Schema();
        List<String> queries = new ArrayList<>();
        boolean readingSchema = true;
        String schemaString = "";

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    readingSchema = false;
                    schema = SchemaUtils.stringToSchema(schemaString);
                    continue;
                }

                if (readingSchema) {
                    schemaString += line + "\n";
                } else {
                    queries.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new InputFileData(schema, queries);
    }

    /***
     * Get a file data and an algorithm and translate the queries from the file with the given algorithm.
     * Save the results in "output.txt"
     * @param fileData schema and queries
     * @param algo such as 2VL
     */
    public static void translateFile(InputFileData fileData, TranslateToSql algo) {
        String outputPath = "output.txt"; // Path to your output file

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            // add schema
            writer.write(SchemaUtils.schemaToString(fileData.getSchema()));
            writer.newLine();
            // add translated queries
            for (String query : fileData.getQueries()) {
                writer.write(algo.translate(query));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
