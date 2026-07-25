package com.monolithinsight.domain;

import java.util.List;

public record JavaConstructorInfo(
        List<JavaParameterInfo> parameters
) {
    public JavaConstructorInfo {
        parameters = List.copyOf(parameters);
    }
}