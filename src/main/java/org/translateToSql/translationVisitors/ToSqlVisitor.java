package org.translateToSql.translationVisitors;

import org.translateToSql.management.AlgorithmResources;

public interface ToSqlVisitor {

    AlgorithmResources getAlgorithmResources();
    void setAlgorithmResources(AlgorithmResources algorithmResources);

}
