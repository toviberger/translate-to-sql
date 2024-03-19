package org.translateToSql;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.translateToSql.twoVL.TwoVL;


@RestController
public class TranslateToSqlController {

    @GetMapping("/twoVL")
    public String index(@RequestParam("query") String query) {
        TwoVL algo = new TwoVL();
        return algo.translate(query);
    }
}