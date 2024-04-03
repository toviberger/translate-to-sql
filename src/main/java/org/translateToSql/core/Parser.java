package org.translateToSql.core;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;

/***
 * Class for parsing a query to AST representation, by using JSqlParser lib, and vice versa
 */
public class Parser {

    /**
     * Gets an SQL query as a string and converts it to an AST - Abstract Syntax Tree - hierarchical object for representing a query,
     * @param sqlString query as a string
     * @return Statement, the root of the AST
     */
    public static Statement parseStringToAst(String sqlString) {
        try {
            return CCJSqlParserUtil.parse(sqlString);
        } catch (JSQLParserException e) {
            throw new RuntimeException("An error occurred: illegal query");
        }
    }

    /**
     * Gets an SQL query as an AST and converts it to String
     * @param statement query as an AST
     * @return representation of the query as a string
     */
    public static String parseAstToString(Statement statement) {
        try {
            return statement.toString();
        } catch (Exception e) {
            throw new RuntimeException("An error occurred: illegal query");
        }
    }
}
