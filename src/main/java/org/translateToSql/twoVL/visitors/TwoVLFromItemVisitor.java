package org.translateToSql.twoVL.visitors;

import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.LateralSubSelect;
import net.sf.jsqlparser.statement.select.ParenthesedFromItem;
import net.sf.jsqlparser.statement.select.ParenthesedSelect;
import net.sf.jsqlparser.statement.select.TableFunction;
import org.translateToSql.ToSqlFromItemVisitor;
import org.translateToSql.VisitorManager;
import org.translateToSql.AlgorithmResources;

public class TwoVLFromItemVisitor implements ToSqlFromItemVisitor {
    private AlgorithmResources algorithmResources;

    @Override
    public void visit(Table tableName) {}

    @Override
    public void visit(ParenthesedSelect selectBody) {
        selectBody.getPlainSelect().accept(this.getVisitorManager().getSelectVisitor());
    }

    @Override
    public void visit(LateralSubSelect lateralSubSelect) {}

    @Override
    public void visit(TableFunction tableFunction) {}

    @Override
    public void visit(ParenthesedFromItem aThis) {
        aThis.getFromItem().accept(this);
    }

    public VisitorManager getVisitorManager() {
        return this.algorithmResources.getVisitorManager();
    }

    @Override
    public void setAlgorithmResources(AlgorithmResources algorithmResources) {
        this.algorithmResources = algorithmResources;
    }

    @Override
    public AlgorithmResources getAlgorithmResources(){
        return this.algorithmResources;
    }
}
