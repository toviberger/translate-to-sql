package org.translateToSql.visitors.translationVisitors;

import net.sf.jsqlparser.statement.StatementVisitor;

public interface ToSqlStatementVisitor extends StatementVisitor, ToSqlVisitor {
}
