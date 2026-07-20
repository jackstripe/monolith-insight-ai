package com.monolithinsight.domain;

public record ClassNode(
        String id,
        JavaClassInfo classInfo
) {

    public static ClassNode from(JavaClassInfo classInfo) {
        String qualifiedName = classInfo.packageName().isBlank()
                ? classInfo.className()
                : classInfo.packageName() + "." + classInfo.className();

        return new ClassNode(qualifiedName, classInfo);
    }
}