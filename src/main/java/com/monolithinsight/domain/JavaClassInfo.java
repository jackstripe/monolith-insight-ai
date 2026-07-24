package com.monolithinsight.domain;

import java.util.List;
import java.util.Optional;

public record JavaClassInfo(
        String packageName,
        String className,
        String type,
        Optional<String> superClass,
        List<String> implementedInterfaces,
        List<String> annotations,
        List<JavaConstructorInfo> constructors,
        List<JavaFieldInfo> fields,
        List<JavaMethodInfo> methods,
        String filePath
) {
    public JavaClassInfo {

        implementedInterfaces = List.copyOf(implementedInterfaces);
        methods = List.copyOf(methods);
        annotations = List.copyOf(annotations);
        constructors = List.copyOf(constructors);
        fields = List.copyOf(fields);
    }
}
