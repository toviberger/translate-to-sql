package org.translateToSql.visitors.translationVisitors;

import net.sf.jsqlparser.statement.select.FromItemVisitor;

public interface ToSqlFromItemVisitor extends FromItemVisitor, ToSqlVisitor {
}
