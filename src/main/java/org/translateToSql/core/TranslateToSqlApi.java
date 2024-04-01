package org.translateToSql.core;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.translateToSql.model.Database;
import org.translateToSql.utils.DatabaseUtils;


@RestController
public class TranslateToSqlApi {

    @PostMapping("/twoVL")
    public String translateFromTwoVL(@RequestParam("query") String query, @RequestParam("dbString") String dbString) {
        Database database = DatabaseUtils.parseDbStringToDbObject(dbString);

        TranslateFromTwoVL algo = new TranslateFromTwoVL(database);
        return algo.translate(query);
    }
}