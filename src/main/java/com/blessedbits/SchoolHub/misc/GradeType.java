package com.blessedbits.SchoolHub.misc;

import lombok.Getter;

@Getter
public enum GradeType {
    TEST("TEST"),
    HOMEWORK("HOMEWORK"),
    CLASSWORK("CLASSWORK"),
    EXAM("EXAM"),
    PROJECT("PROJECT");

    private final String typeName;

    GradeType(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public String toString() {
        return getTypeName();
    }

}
