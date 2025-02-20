package com.blessedbits.SchoolHub.projections.mappers;

import com.blessedbits.SchoolHub.models.School;
import com.blessedbits.SchoolHub.projections.dto.CourseDto;
import com.blessedbits.SchoolHub.projections.dto.SchoolDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SchoolMapper {
    SchoolMapper INSTANCE = Mappers.getMapper(SchoolMapper.class);

    default SchoolDto toSchoolDto(School school, List<String> include) {
        SchoolDto schoolDto;
        if (include == null || include.isEmpty()) {
            schoolDto = BasicDtoMapper.toSchoolDto(school);
            return schoolDto;
        } else {
            schoolDto = BasicDtoMapper.toBasicSchoolDto(school);
        }
        if (include.contains("classes")) {
            schoolDto.setClasses(school.getClasses().stream()
                    .map(BasicDtoMapper::toClassDto)
                    .toList());
        }
        if (include.contains("courses")) {
            schoolDto.setCourses(school.getCourses().stream()
                    .map(BasicDtoMapper::toCourseDto)
                    .toList());
        }
        if (include.contains("users")) {
            schoolDto.setUsers(school.getUsers().stream()
                    .map(BasicDtoMapper::toUserDto)
                    .toList());
        }
        if (include.contains("achievements")) {
            schoolDto.setAchievements(school.getAchievements().stream()
                    .map(BasicDtoMapper::toAchievementDto)
                    .toList());
        }
        return schoolDto;
    }
}
