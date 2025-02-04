package com.blessedbits.SchoolHub.projections.mappers;

import com.blessedbits.SchoolHub.models.ClassEntity;
import com.blessedbits.SchoolHub.projections.dto.*;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ClassMapper {
    ClassMapper INSTANCE = Mappers.getMapper(ClassMapper.class);

    default ClassDto toClassDto(ClassEntity classEntity, @Context List<String> include) {
        ClassDto classDto = new ClassDto();
        classDto.setId(classEntity.getId());
        classDto.setName(classEntity.getName());
        if (include == null || include.isEmpty()) {
            classDto.setHomeroomTeacherId(classEntity.getHomeroomTeacher().getId());
            classDto.setSchoolId(classEntity.getSchool().getId());
            return classDto;
        }
        if (include.contains("homeroomTeacher")) {
            classDto.setHomeroomTeacher(BasicDtoMapper.toUserDto(classEntity.getHomeroomTeacher()));
        } else {
            classDto.setHomeroomTeacherId(classEntity.getHomeroomTeacher().getId());
        }
        if (include.contains("school")) {
            classDto.setSchool(BasicDtoMapper.toSchoolDto(classEntity.getSchool()));
        } else {
            classDto.setSchoolId(classEntity.getSchool().getId());
        }
        if (include.contains("students")) {
            classDto.setStudents(classEntity.getStudents().stream()
                    .map(BasicDtoMapper::toUserDto)
                    .collect(Collectors.toList()));
        }
        if (include.contains("courses")) {
            classDto.setCourses(classEntity.getCourses().stream()
                    .map(BasicDtoMapper::toCourseDto)
                    .collect(Collectors.toList()));
        }
        return classDto;
    }
}
