package org.translateToSql.core;

import net.sf.jsqlparser.statement.Statement;
import org.translateToSql.model.Database;
import org.translateToSql.utils.Parser;
import org.translateToSql.visitors.translationVisitors.twoVLVisitors.*;

/***
 * A concrete implementation of the abstract TranslateToSql class, designed to perform specific translation tasks on
 * SQL queries (their AST representations), using two-valued logic (2VL). Implements the translation algorithm based on
 * the principles described in the article "Handling SQL Nulls with Two-Valued Logic".
 */

public class TwoVL extends TranslateToSql {

    public TwoVL(Database db){
        super(new TrTExpressionVisitor(), new TwoVLSelectItemVisitor(), new TwoVLSelectVisitor(), new TwoVLFromItemVisitor(), new TwoVLStatementVisitor(), db);
    }

    @Override
    public String translate(String query){
        try {
            // convert query to AST representation
            Statement statement = Parser.parseStringToAst(query);
//            // validate
//            this.validate(statement);
            // translate toSQL
            statement.accept(this.getVisitorManager().getStatementVisitor());
            // convert to string
            return Parser.parseAstToString(statement);
        }
        catch (Exception e){
            return "illegal query";
        }

    }
}
