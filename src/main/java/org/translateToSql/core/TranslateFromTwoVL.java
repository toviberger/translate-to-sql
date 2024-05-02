package org.translateToSql.core;

import net.sf.jsqlparser.statement.Statement;
import org.translateToSql.model.Schema;
import org.translateToSql.translationVisitors.fromTwoVLVisitors.*;

/***
 * A concrete implementation of the abstract TranslateToSql class, designed to perform specific translation tasks on
 * SQL queries, using two-valued logic (2VL). Implements the translation algorithm based on
 * the principles described in the article 'Handling SQL Nulls with Two-Valued Logic' by Libkin and Peterfreund (2022).
 */

public class TranslateFromTwoVL extends TranslateToSql {

    public TranslateFromTwoVL(Schema schema){
        super(
                new TrTExpressionVisitor(),
                new TwoVLSelectItemVisitor(),
                new TwoVLSelectVisitor(),
                new TwoVLFromItemVisitor(),
                new TwoVLStatementVisitor(),
                schema
        );
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
