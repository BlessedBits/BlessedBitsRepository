package com.blessedbits.SchoolHub.misc;

import com.blessedbits.SchoolHub.models.ClassEntity;
import com.blessedbits.SchoolHub.models.Course;
import com.blessedbits.SchoolHub.models.School;
import com.blessedbits.SchoolHub.models.UserEntity;

public class RoleBasedAccessUtils {
    public static boolean canAccessClass(UserEntity user, ClassEntity classEntity) {
        if (user.hasRole(RoleType.PLATFORM_ADMIN)) {
            return true;
        }
        if (user.hasRole(RoleType.SCHOOL_ADMIN) || user.hasRole(RoleType.TEACHER)) {
            return classEntity.getSchool().equals(user.getSchool());
        }
        if (user.hasRole(RoleType.STUDENT)) {
            return user.getUserClass().equals(classEntity);
        }
        return false;
    }

    public static boolean canModifyClass(UserEntity user, ClassEntity classEntity) {
        if (user.hasRole(RoleType.PLATFORM_ADMIN)) {
            return true;
        }
        if (user.hasRole(RoleType.SCHOOL_ADMIN)) {
            return classEntity.getSchool().equals(user.getSchool());
        }
        return false;
    }

    public static boolean canAccessCourse(UserEntity user, Course course) {
        if (user.hasRole(RoleType.PLATFORM_ADMIN)) {
            return true;
        }
        if (user.hasRole(RoleType.SCHOOL_ADMIN) || user.hasRole(RoleType.TEACHER)) {
            return course.getSchool().equals(user.getSchool());
        }
        if (user.hasRole(RoleType.STUDENT)) {
            return user.getUserClass().getCourses().contains(course);
        }
        return false;
    }

    public static boolean canModifyCourse(UserEntity user, Course course) {
        if (user.hasRole(RoleType.PLATFORM_ADMIN)) {
            return true;
        }
        if (user.hasRole(RoleType.SCHOOL_ADMIN) || user.hasRole(RoleType.TEACHER)) {
            return course.getSchool().equals(user.getSchool());
        }
        return false;
    }

    public static boolean canModifySchool(UserEntity user, School school) {
        if (user.hasRole(RoleType.PLATFORM_ADMIN)) {
            return true;
        }
        if (user.hasRole(RoleType.SCHOOL_ADMIN)) {
            return school.equals(user.getSchool());
        }
        return false;
    }
}
