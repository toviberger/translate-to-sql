package org.translateToSql.visitors.translationVisitors;

import net.sf.jsqlparser.expression.ExpressionVisitor;

public interface ToSqlExpressionVisitor extends ExpressionVisitor, ToSqlVisitor {
}
