package com.monolithinsight.domain;

import java.util.Comparator;
import java.util.List;

public class GenerateHotspotsReportUseCase {

    public HotspotsReport execute(GraphMetrics metrics) {

        List<ClassHotspot> classHotspotList = metrics.classes()
                .stream()
                .sorted(
                        Comparator.comparingInt(ClassMetrics::totalDependencies)
                        .thenComparing(ClassMetrics::classId)
                )
                .map(classMetrics -> new ClassHotspot(
                        classMetrics.classId(),
                        classMetrics.incomingDependencies(),
                        classMetrics.outgoingDependencies(),
                        classMetrics.totalDependencies(),
                        classify(classMetrics.totalDependencies())
                )).toList();

        return new HotspotsReport(classHotspotList);

    }

    private CouplingLevel classify(int totalDependencies) {
        if (totalDependencies <= 5) {
            return CouplingLevel.LOW;
        }

        if (totalDependencies <= 10) {
            return CouplingLevel.MEDIUM;
        }

        return CouplingLevel.HIGH;
    }
}
