package org.translateToSql.core;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.translateToSql.model.DatabaseMetadata;
import org.translateToSql.utils.DatabaseUtils;

/***
 *  Exposes the translation functionality as an API endpoint, allowing clients to submit queries and database
 *  schema representations for translation.
 */
@RestController
public class TranslateToSqlApi {

    @PostMapping("/twoVL")
    public String translateFromTwoVL(@RequestParam("query") String query, @RequestParam("dbString") String dbString) {
        DatabaseMetadata database = DatabaseUtils.parseDbStringToDbObject(dbString);

        TranslateFromTwoVL algo = new TranslateFromTwoVL(database);
        return algo.translate(query);
    }
}