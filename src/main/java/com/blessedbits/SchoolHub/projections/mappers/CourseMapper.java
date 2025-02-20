package com.blessedbits.SchoolHub.projections.mappers;

import com.blessedbits.SchoolHub.models.Course;
import com.blessedbits.SchoolHub.projections.dto.CourseDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CourseMapper {
    CourseMapper INSTANCE = Mappers.getMapper(CourseMapper.class);

    default CourseDto toCourseDto(Course course, List<String> include) {
        CourseDto courseDto;
        if (include == null || include.isEmpty()) {
            courseDto = BasicDtoMapper.toCourseDto(course);
            return courseDto;
        } else {
            courseDto = BasicDtoMapper.toBasicCourseDto(course);
        }
        if (include.contains("school")) {
            courseDto.setSchool(BasicDtoMapper.toSchoolDto(course.getSchool()));
        } else {
            courseDto.setSchoolId(course.getSchool().getId());
        }
        if (include.contains("modules")) {
            courseDto.setModules(course.getModules().stream()
                    .map(BasicDtoMapper::toModuleDto)
                    .toList());
        }
        if (include.contains("classes")) {
            courseDto.setClasses(course.getClasses().stream()
                    .map(BasicDtoMapper::toClassDto)
                    .toList());
        }
        if (include.contains("teachers")) {
            courseDto.setTeachers(course.getTeachers().stream()
                    .map(BasicDtoMapper::toUserDto)
                    .toList());
        }
        return courseDto;
    }
}
