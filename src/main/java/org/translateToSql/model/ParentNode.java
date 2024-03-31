package org.translateToSql.model;

import net.sf.jsqlparser.expression.Expression;
import org.translateToSql.model.ChildPosition;


/***
 * Class for representing a parent node in the AST we get from JSqlParser.
 * It keeps the expression that is in the parent and which left or right child of the parent the son is in
 */
public class ParentNode {
    private Expression parentExpression;
    private ChildPosition childPosition;

    public ChildPosition getChildPosition() {
        return childPosition;
    }

    public Expression getParentExpression() {
        return parentExpression;
    }

    public void setParent(Expression expression, ChildPosition position) {
        this.parentExpression = expression;
        this.childPosition = position;
    }
}
