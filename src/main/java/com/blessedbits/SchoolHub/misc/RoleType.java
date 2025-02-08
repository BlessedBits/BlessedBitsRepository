package com.blessedbits.SchoolHub.misc;

import lombok.Getter;

@Getter
public enum RoleType {
    USER("USER"),
    STUDENT("STUDENT"),
    TEACHER("TEACHER"),
    SCHOOL_ADMIN("SCHOOL_ADMIN"),
    PLATFORM_ADMIN("PLATFORM_ADMIN");

    private final String roleName;

    RoleType(String roleName) {
        this.roleName = roleName;
    }

    @Override
    public String toString() {
        return getRoleName();
    }
}
