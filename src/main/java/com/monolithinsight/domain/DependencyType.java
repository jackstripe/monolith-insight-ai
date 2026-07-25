package com.monolithinsight.domain;

public enum DependencyType {
    FIELD,
    CONSTRUCTOR,
    METHOD_PARAMETER,
    RETURN_TYPE,
    INHERITANCE,
    IMPLEMENTS
}
