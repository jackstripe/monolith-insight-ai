package com.monolithinsight.domain;

import java.util.List;

public record JavaMethodInfo(
        String name,
        String returnType,
        List<JavaParameterInfo> parameters
) {
    public JavaMethodInfo {
        parameters = List.copyOf(parameters);
    }
}