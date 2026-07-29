package com.monolithinsight.application;

import com.monolithinsight.domain.ClassMetrics;
import com.monolithinsight.domain.CoupledClass;
import com.monolithinsight.domain.CouplingReport;
import com.monolithinsight.domain.GraphMetrics;
import java.util.Comparator;

public class FindMostCoupledClassesUseCase {

    public CouplingReport execute(
            GraphMetrics metrics,
            int limit
    ) {

            return new CouplingReport( metrics
                    .classes()
                    .stream()
                    .sorted(
                            Comparator.comparingInt(ClassMetrics::totalDependencies)
                                    .reversed()
                                    .thenComparing(ClassMetrics::classId)
                    )
                    .limit(limit)
                    .map(classMetrics -> new CoupledClass(
                            classMetrics.classId(),
                            classMetrics.incomingDependencies(),
                            classMetrics.outgoingDependencies(),
                            classMetrics.totalDependencies()
                    )).toList()
            );
    }
}
