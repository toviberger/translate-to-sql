package org.translateToSql.visitors.translationVisitors;

import net.sf.jsqlparser.statement.select.SelectVisitor;

public interface ToSqlSelectVisitor extends SelectVisitor, ToSqlVisitor {
}
