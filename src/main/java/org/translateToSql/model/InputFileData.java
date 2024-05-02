package org.translateToSql.model;

import java.util.ArrayList;
import java.util.List;

/***
 * This class is used to store the parsed schema (as a map of table names to columns) and queries (as a list of strings).
 */
public class InputFileData {
    private Schema schema;
    private List<String> queries = new ArrayList<>();

    public InputFileData(Schema schema, List<String> queries) {
        this.schema = schema;
        this.queries = queries;
    }

    public Schema getSchema() {
        return schema;
    }

    public List<String> getQueries() {
        return queries;
    }
}
