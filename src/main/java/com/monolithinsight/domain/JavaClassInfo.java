package com.monolithinsight.domain;

import java.util.List;

public record JavaClassInfo(
        String packageName,
        String className,
        String type,
        List<String> annotations,
        List<JavaConstructorInfo> constructors,
        List<JavaFieldInfo> fields,
        List<String> methods,
        String filePath
) {
    public JavaClassInfo {

        methods = List.copyOf(methods);
        annotations = List.copyOf(annotations);
        constructors = List.copyOf(constructors);
        fields = List.copyOf(fields);
    }
}
