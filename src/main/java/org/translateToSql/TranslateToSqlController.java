package org.translateToSql;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.translateToSql.database.Database;
import org.translateToSql.database.DatabaseUtils;
import org.translateToSql.twoVL.TwoVL;


@RestController
public class TranslateToSqlController {

    @PostMapping("/twoVL")
    public String twoVL(@RequestParam("query") String query, @RequestParam("dbString") String dbString) {
        Database dataBase = new Database();
        DatabaseUtils.parseDbStringToDbObject(dbString, dataBase);
        TwoVL algo = new TwoVL(dataBase);
        return algo.translate(query);
    }
}