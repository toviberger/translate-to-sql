package org.translateToSql.translationVisitors;

import net.sf.jsqlparser.statement.select.FromItemVisitor;

public interface ToSqlFromItemVisitor extends FromItemVisitor, ToSqlVisitor {
}
