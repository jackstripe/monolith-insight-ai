package com.monolithinsight.application;

import com.monolithinsight.domain.ImpactAnalysisReport;
import com.monolithinsight.domain.ProjectGraph;

import java.util.List;

public class AnalyzeImpactUseCase {

    public ImpactAnalysisReport execute(
            ProjectGraph graph,
            String changedClassId
    ) {
        return new ImpactAnalysisReport(List.of());
    }
}
