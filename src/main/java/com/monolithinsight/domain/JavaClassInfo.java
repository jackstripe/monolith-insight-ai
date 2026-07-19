package com.monolithinsight.domain;

import java.util.List;

public record JavaClassInfo(
        String packageName,
        String className,
        String type,
        List<String> annotations,
        List<String> constructors,
        List<JavaFieldInfo> fields,
        List<String> methods,
        String filePath
) {


}
