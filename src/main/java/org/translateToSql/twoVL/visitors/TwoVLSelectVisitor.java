package org.translateToSql.twoVL.visitors;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.*;
import org.translateToSql.ToSqlSelectVisitor;
import org.translateToSql.VisitorManager;
import org.translateToSql.AlgorithmResources;
import org.translateToSql.utils.ExpressionUtils;
import org.translateToSql.utils.Parser;
import org.translateToSql.utils.SelectUtils;

import java.util.Map;

public class TwoVLSelectVisitor implements ToSqlSelectVisitor {

    private AlgorithmResources algorithmResources;

    @Override
    public void visit(ParenthesedSelect parenthesedSelect) {
        parenthesedSelect.getSelect().accept(this);
    }

    @Override
    public void visit(PlainSelect plainSelect) {
        // if there is HAVING
        if (plainSelect.getHaving() != null)
            handleSelectWithHaving(plainSelect);
        else
            handleSelectWithoutHaving(plainSelect);
    }

    @Override
    public void visit(SetOperationList setOpList)
    {
        setOpList.getSelects().forEach(select -> select.accept(this));
    }

    @Override
    public void visit(WithItem withItem) {

    }

    @Override
    public void visit(Values aThis) {

    }

    @Override
    public void visit(LateralSubSelect lateralSubSelect) {

    }

    @Override
    public void visit(TableStatement tableStatement) {

    }

    public VisitorManager getVisitorManager() {
        return this.algorithmResources.getVisitorManager();
    }

    /***
     * translates a query that doesn't have having. translates select items, from items and the conditions in where
     * @param plainSelect SELECT ... FROM ... (WHERE ...)
     */
    private void handleSelectWithoutHaving(PlainSelect plainSelect) {
        // visit select items
        plainSelect.getSelectItems().forEach(selectItem -> selectItem.accept(this.getVisitorManager().getSelectItemVisitor()));

        // visit from items
        plainSelect.getFromItem().accept(this.getVisitorManager().getFromItemVisitor());

        // visit where conditions
        if (plainSelect.getWhere() != null) {
            // dummy root for the where AST
            AndExpression dummyRoot = new AndExpression(new Column("TRUE"), plainSelect.getWhere());
            plainSelect.setWhere(dummyRoot);
            plainSelect.getWhere().accept(this.getVisitorManager().getExpressionVisitor());
            plainSelect.setWhere(dummyRoot.getRightExpression());
        }
    }

    /***
     * A query E with having is translated to - SELECT * FROM toSQL(E) AS SUB_QUERY_NAME WHERE toSQL(E.HAVING)
     * @param plainSelect SELECT ... FROM ... (WHERE ...) GROUP BY ... HAVING ...
     */
    private void handleSelectWithHaving(PlainSelect plainSelect) {
        Map<String, String> selectItemsMapping = SelectUtils.handleSelectItemsMapping(plainSelect);

        // fix having to use the new aliases
        Expression having = plainSelect.getHaving();
        having.accept(new TwoVLHavingVisitor(selectItemsMapping));

        // define a new query - SELECT * FROM(E without HAVING) WHERE (E.HAVING)
        plainSelect.setHaving(null);
        PlainSelect newQuery = (PlainSelect) Parser.parseStringToAst
                ("SELECT * " +
                "FROM " + new Parenthesis(plainSelect) + " AS " + ExpressionUtils.SUB_QUERY_NAME +
                " WHERE " + having
                );
        plainSelect.setSelectItems(newQuery.getSelectItems());
        plainSelect.setFromItem(newQuery.getFromItem());
        plainSelect.setWhere(newQuery.getWhere());
        plainSelect.setGroupByElement(null);

        // translate the new query
        plainSelect.accept(this.algorithmResources.getVisitorManager().getStatementVisitor());
    }

    @Override
    public void setAlgorithmResources(AlgorithmResources algorithmResources) {
        this.algorithmResources = algorithmResources;
    }

    @Override
    public AlgorithmResources getAlgorithmResources(){
        return this.algorithmResources;
    }
}
