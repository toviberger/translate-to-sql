package org.translateToSql.api;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.translateToSql.model.Database;
import org.translateToSql.utils.DatabaseUtils;
import org.translateToSql.core.TwoVL;


@RestController
public class TranslateToSqlController {

    @PostMapping("/twoVL")
    public String twoVL(@RequestParam("query") String query, @RequestParam("dbString") String dbString) {
        Database database = DatabaseUtils.parseDbStringToDbObject(dbString);

        TwoVL algo = new TwoVL(database);
        return algo.translate(query);
    }
}