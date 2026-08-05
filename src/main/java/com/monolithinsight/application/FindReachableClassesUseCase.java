package com.monolithinsight.application;

import com.monolithinsight.domain.ProjectGraph;
import com.monolithinsight.domain.ReachabilityReport;

import java.util.List;

public class FindReachableClassesUseCase {



    public ReachabilityReport execute(ProjectGraph graph, String classId){

        return new ReachabilityReport(List.of());

    }
}
