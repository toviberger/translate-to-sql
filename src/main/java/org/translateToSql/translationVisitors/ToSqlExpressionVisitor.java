package org.translateToSql.translationVisitors;

import net.sf.jsqlparser.expression.ExpressionVisitor;

public interface ToSqlExpressionVisitor extends ExpressionVisitor, ToSqlVisitor {
}
