package org.translateToSql.core;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.translateToSql.model.Schema;
import org.translateToSql.utils.SchemaUtils;

/***
 *  Exposes the translation functionality as an API endpoint, allowing clients to submit queries and database
 *  schema representations for translation.
 */
@RestController
public class TranslateToSqlApi {

    @PostMapping("/twoVL")
    public String translateFromTwoVL(@RequestParam("query") String query, @RequestParam("schemaString") String schemaString) {
        Schema database = SchemaUtils.stringToSchema(schemaString);

        TranslateFromTwoVL algo = new TranslateFromTwoVL(database);
        return algo.translate(query);
    }
}