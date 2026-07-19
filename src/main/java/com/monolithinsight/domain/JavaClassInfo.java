package com.monolithinsight.domain;

import java.util.List;

public record JavaClassInfo(
        String packageName,
        String className,
        String type,
        List<String> methods
) {


}
