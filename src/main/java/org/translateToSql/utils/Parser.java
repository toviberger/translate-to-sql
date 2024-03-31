package org.translateToSql.utils;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;

/***
 * Class for parsing a query to and AST representation, by using JSqlParser lib, and vice versa
 */
public class Parser {

    /**
     * Gets an SQL query as a string and converts it to a Statement object - hierarchical object for representing a query,
     * like an AST - Abstract Syntax Tree
     * @param sqlString query as a string
     * @return representation of the query as a Statement
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
