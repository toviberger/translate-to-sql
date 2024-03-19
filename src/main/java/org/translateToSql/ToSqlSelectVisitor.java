package org.translateToSql;

import net.sf.jsqlparser.statement.select.SelectVisitor;

public interface ToSqlSelectVisitor extends SelectVisitor, ToSqlVisitor {
}
