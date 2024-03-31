package org.translateToSql.visitors.translationVisitors;

import net.sf.jsqlparser.statement.select.SelectItemVisitor;

public interface ToSqlSelectItemVisitor extends SelectItemVisitor, ToSqlVisitor {
}
