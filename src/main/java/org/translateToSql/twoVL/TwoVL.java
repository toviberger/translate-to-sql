package org.translateToSql.twoVL;

import net.sf.jsqlparser.statement.Statement;
import org.translateToSql.*;
import org.translateToSql.twoVL.visitors.*;
import org.translateToSql.utils.Parser;

/***
 * A concrete implementation of the abstract TranslateToSql class, designed to perform specific translation tasks on
 * SQL queries (their AST representations), using two-valued logic (2VL). Implements the translation algorithm based on
 * the principles described in the article "Handling SQL Nulls with Two-Valued Logic".
 */

public class TwoVL extends TranslateToSql {

    public TwoVL(){
        super(new TrTExpressionVisitor(), new TwoVLSelectItemVisitor(), new TwoVLSelectVisitor(), new TwoVLFromItemVisitor(), new TwoVLStatementVisitor());
    }

    @Override
    public String translate(String query){
        try {
            // convert query to AST representation
            Statement statement = Parser.parseStringToAst(query);
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
