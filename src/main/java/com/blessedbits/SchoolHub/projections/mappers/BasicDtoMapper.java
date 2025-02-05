package com.blessedbits.SchoolHub.projections.mappers;

import com.blessedbits.SchoolHub.models.*;
import com.blessedbits.SchoolHub.projections.dto.*;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BasicDtoMapper {
    static UserDto toBasicUserDto(UserEntity user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        userDto.setConfirmed(user.getIsConfirmed());
        userDto.setProfileImage(user.getProfileImage());
        userDto.setDuty(user.getDuty());
        return userDto;
    }

    static UserDto toUserDto(UserEntity user) {
        UserDto userDto = toBasicUserDto(user);
        if (user.getUserClass() != null) {
            userDto.setUserClassId(user.getUserClass().getId());
        }
        if (user.getSchool() != null) {
            userDto.setSchoolId(user.getSchool().getId());
        }
        return userDto;
    }

    static RoleDto toBasicRoleDto(Role role) {
        RoleDto roleDto = new RoleDto();
        roleDto.setId(role.getId());
        roleDto.setName(role.getName());
        return roleDto;
    }

    static RoleDto toRoleDto(Role role) {
        RoleDto roleDto = toBasicRoleDto(role);
        return roleDto;
    }

    static SchoolDto toBasicSchoolDto(School school) {
        SchoolDto schoolDto = new SchoolDto();
        schoolDto.setId(school.getId());
        schoolDto.setName(school.getName());
        schoolDto.setAddress(school.getAddress());
        schoolDto.setLogo(school.getLogo());
        return schoolDto;
    }

    static SchoolDto toSchoolDto(School school) {
        SchoolDto schoolDto = toBasicSchoolDto(school);
        return schoolDto;
    }

    static CourseDto toBasicCourseDto(Course course) {
        CourseDto courseDto = new CourseDto();
        courseDto.setId(course.getId());
        courseDto.setName(course.getName());
        return courseDto;
    }

    static CourseDto toCourseDto(Course course) {
        CourseDto courseDto = toBasicCourseDto(course);
        courseDto.setSchoolId(course.getSchool().getId());
        return courseDto;
    }

    static ClassDto toBasicClassDto(ClassEntity classEntity) {
        ClassDto classDto = new ClassDto();
        classDto.setId(classEntity.getId());
        classDto.setName(classEntity.getName());
        return classDto;
    }

    static ClassDto toClassDto(ClassEntity classEntity) {
        ClassDto classDto = toBasicClassDto(classEntity);
        classDto.setHomeroomTeacherId(classEntity.getHomeroomTeacher().getId());
        classDto.setSchoolId(classEntity.getSchool().getId());
        return classDto;
    }
}
