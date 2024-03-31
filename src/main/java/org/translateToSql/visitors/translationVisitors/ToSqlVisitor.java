package org.translateToSql.visitors.translationVisitors;

import org.translateToSql.management.AlgorithmResources;

public interface ToSqlVisitor {

    AlgorithmResources getAlgorithmResources();
    void setAlgorithmResources(AlgorithmResources algorithmResources);

}
