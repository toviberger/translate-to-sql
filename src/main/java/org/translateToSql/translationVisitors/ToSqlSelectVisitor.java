package org.translateToSql.translationVisitors;

import net.sf.jsqlparser.statement.select.SelectVisitor;

public interface ToSqlSelectVisitor extends SelectVisitor, ToSqlVisitor {
}
