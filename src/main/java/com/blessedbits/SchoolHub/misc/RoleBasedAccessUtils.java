package com.blessedbits.SchoolHub.misc;

import com.blessedbits.SchoolHub.dto.GradeDto;
import com.blessedbits.SchoolHub.models.ClassEntity;
import com.blessedbits.SchoolHub.models.Course;
import com.blessedbits.SchoolHub.models.Grade;
import com.blessedbits.SchoolHub.models.School;
import com.blessedbits.SchoolHub.models.Submission;
import com.blessedbits.SchoolHub.models.UserEntity;
import com.blessedbits.SchoolHub.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class RoleBasedAccessUtils {
    private MiscRepository miscRepository;

    @Autowired
    private UserRepository userRepository;

    public RoleBasedAccessUtils(MiscRepository miscRepository) {
        this.miscRepository = miscRepository;
    }

    public boolean canAccessClass(UserEntity user, ClassEntity classEntity) {
        if (user.hasRole(RoleType.PLATFORM_ADMIN)) {
            return true;
        }
        if (user.hasRole(RoleType.SCHOOL_ADMIN) || user.hasRole(RoleType.TEACHER)) {
            return Objects.equals(classEntity.getSchool().getId(), user.getSchool().getId());
        }
        if (user.hasRole(RoleType.STUDENT)) {
            return user.getUserClass().getId() == classEntity.getId();
        }
        return false;
    }

    public boolean canModifyClass(UserEntity user, ClassEntity classEntity) {
        if (user.hasRole(RoleType.PLATFORM_ADMIN)) {
            return true;
        }
        if (user.hasRole(RoleType.SCHOOL_ADMIN)) {
            return Objects.equals(classEntity.getSchool().getId(), user.getSchool().getId());
        }
        return false;
    }

    public boolean canAccessCourse(UserEntity user, Course course) {
        if (user.hasRole(RoleType.PLATFORM_ADMIN)) {
            return true;
        }
        if (user.hasRole(RoleType.SCHOOL_ADMIN) || user.hasRole(RoleType.TEACHER)) {
            return Objects.equals(course.getSchool().getId(), user.getSchool().getId());
        }
        if (user.hasRole(RoleType.STUDENT)) {
            return miscRepository.classHasCourse(user.getUserClass().getId(), course.getId());
        }
        return false;
    }

    public boolean canModifyCourse(UserEntity user, Course course) {
        if (user.hasRole(RoleType.PLATFORM_ADMIN)) {
            return true;
        }
        if (user.hasRole(RoleType.SCHOOL_ADMIN) || user.hasRole(RoleType.TEACHER)) {
            return Objects.equals(course.getSchool().getId(), user.getSchool().getId());
        }
        return false;
    }

    public boolean canAccessSchool(UserEntity user, School school) {
        if (user.hasRole(RoleType.PLATFORM_ADMIN)) {
            return true;
        }
        if (user.hasRole(RoleType.SCHOOL_ADMIN) || user.hasRole(RoleType.TEACHER) || user.hasRole(RoleType.STUDENT)) {
            return Objects.equals(school.getId(), user.getSchool().getId());
        }
        return false;
    }

    public boolean canModifySchool(UserEntity user, School school) {
        if (user.hasRole(RoleType.PLATFORM_ADMIN)) {
            return true;
        }
        if (user.hasRole(RoleType.SCHOOL_ADMIN)) {
            return Objects.equals(school.getId(), user.getSchool().getId());
        }
        return false;
    }

    public boolean canAccessUser(UserEntity user, UserEntity targetUser) {
        if (user.hasRole(RoleType.PLATFORM_ADMIN)) {
            return true;
        }
        if (user.hasRole(RoleType.SCHOOL_ADMIN) || user.hasRole(RoleType.TEACHER)) {
            return Objects.equals(targetUser.getSchool().getId(), user.getSchool().getId());
        }
        if (user.hasRole(RoleType.STUDENT) || user.hasRole(RoleType.USER)) {
            return targetUser.getId() == user.getId();
        }
        return false;
    }

    public boolean canModifyUser(UserEntity user, UserEntity targetUser) {
        if (user.hasRole(RoleType.PLATFORM_ADMIN)) {
            return true;
        }
        if (user.hasRole(RoleType.SCHOOL_ADMIN)) {
            return Objects.equals(targetUser.getSchool().getId(), user.getSchool().getId());
        }
        if (user.hasRole(RoleType.TEACHER) || user.hasRole(RoleType.STUDENT) || user.hasRole(RoleType.USER)) {
            return user.getId() == targetUser.getId();
        }
        return false;
    }

    public boolean canModifyUserRole(UserEntity user, UserEntity targetUser, RoleType targetRole) {
        if (user.hasRole(RoleType.PLATFORM_ADMIN)) {
            return true;
        }
        if (user.hasRole(RoleType.SCHOOL_ADMIN)) {
            if (targetUser.hasRole(RoleType.PLATFORM_ADMIN)) {
                return false;
            }
            if (targetRole == RoleType.PLATFORM_ADMIN) {
                return false;
            }
            return Objects.equals(targetUser.getSchool().getId(), user.getSchool().getId());
        }
        return false;
    }

    public boolean canModifyGrade(UserEntity teacher, GradeDto dto)
    {
        if (teacher.hasRole(RoleType.PLATFORM_ADMIN)) {
            return true;
        }
        if (teacher.hasRole(RoleType.SCHOOL_ADMIN)) {
            UserEntity student = userRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student with ID " + dto.getStudentId() + " wasn't found"));
            return Objects.equals(student.getSchool().getId(), teacher.getSchool().getId());
        }
        if(teacher.hasRole(RoleType.TEACHER))
        {
            return teacher.getCourses().stream().anyMatch(course -> course.getId() == dto.getCourseId());
        }
        return false;
    }

    public boolean canDeleteSubmission(Submission submission, UserEntity user)
    {
        if(user.hasRole(RoleType.STUDENT) || user.hasRole(RoleType.TEACHER))
        {
            Course course = submission.getAssignment().getModule().getCourse();
            boolean isTeacher = course.getTeachers().stream()
            .anyMatch(teacher -> teacher.getId() == user.getId());
            
            if(submission.getStudent().getId() == user.getId() || isTeacher)
            {
                return true;
            }
        }
        return false;
    }
    

}
