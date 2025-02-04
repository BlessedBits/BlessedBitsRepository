package com.blessedbits.SchoolHub.projections.mappers;

import com.blessedbits.SchoolHub.models.Course;
import com.blessedbits.SchoolHub.models.School;
import com.blessedbits.SchoolHub.models.UserEntity;
import com.blessedbits.SchoolHub.projections.dto.CourseDto;
import com.blessedbits.SchoolHub.projections.dto.SchoolDto;
import com.blessedbits.SchoolHub.projections.dto.UserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BasicDtoMapper {
    static UserDto toUserDto(UserEntity user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        userDto.setConfirmed(user.getIsConfirmed());
        userDto.setProfileImage(user.getProfileImage());
        userDto.setDuty(user.getDuty());
        return userDto;
    }

    static SchoolDto toSchoolDto(School school) {
        SchoolDto schoolDto = new SchoolDto();
        schoolDto.setId(school.getId());
        schoolDto.setName(school.getName());
        schoolDto.setAddress(school.getAddress());
        schoolDto.setLogo(school.getLogo());
        return schoolDto;
    }

    static CourseDto toCourseDto(Course course) {
        CourseDto courseDto = new CourseDto();
        courseDto.setId(course.getId());
        courseDto.setName(course.getName());
        return courseDto;
    }
}
