package org.translateToSql;

import net.sf.jsqlparser.statement.select.FromItemVisitor;

public interface ToSqlFromItemVisitor extends FromItemVisitor, ToSqlVisitor {
}
