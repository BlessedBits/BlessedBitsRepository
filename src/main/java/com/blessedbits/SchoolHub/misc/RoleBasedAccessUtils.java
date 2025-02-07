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
            return classEntity.getSchool().getId() == user.getSchool().getId();
        }
        if (user.hasRole(RoleType.STUDENT)) {
            return user.getUserClass().getId() == classEntity.getId();
        }
        return false;
    }

    public static boolean canModifyClass(UserEntity user, ClassEntity classEntity) {
        if (user.hasRole(RoleType.PLATFORM_ADMIN)) {
            return true;
        }
        if (user.hasRole(RoleType.SCHOOL_ADMIN)) {
            return classEntity.getSchool().getId() == user.getSchool().getId();
        }
        return false;
    }

    public static boolean canAccessCourse(UserEntity user, Course course) {
        if (user.hasRole(RoleType.PLATFORM_ADMIN)) {
            return true;
        }
        if (user.hasRole(RoleType.SCHOOL_ADMIN) || user.hasRole(RoleType.TEACHER)) {
            return course.getSchool().getId() == user.getSchool().getId();
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
            return course.getSchool().getId() == user.getSchool().getId();
        }
        return false;
    }

    public static boolean canAccessSchool(UserEntity user, School school) {
        if (user.hasRole(RoleType.PLATFORM_ADMIN)) {
            return true;
        }
        if (user.hasRole(RoleType.SCHOOL_ADMIN) || user.hasRole(RoleType.TEACHER) || user.hasRole(RoleType.STUDENT)) {
            return school.getId() == user.getSchool().getId();
        }
        return false;
    }

    public static boolean canModifySchool(UserEntity user, School school) {
        if (user.hasRole(RoleType.PLATFORM_ADMIN)) {
            return true;
        }
        if (user.hasRole(RoleType.SCHOOL_ADMIN)) {
            return school.getId() == user.getSchool().getId();
        }
        return false;
    }

    public static boolean canModifyUser(UserEntity user, UserEntity targetUser) {
        if (user.hasRole(RoleType.PLATFORM_ADMIN)) {
            return true;
        }
        if (user.hasRole(RoleType.SCHOOL_ADMIN)) {
            return targetUser.getSchool() == null || (targetUser.getSchool().getId() == user.getSchool().getId());
        }
        return false;
    }
}
