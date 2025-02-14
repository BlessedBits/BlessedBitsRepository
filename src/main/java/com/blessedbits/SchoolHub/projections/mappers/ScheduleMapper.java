package com.blessedbits.SchoolHub.projections.mappers;

import com.blessedbits.SchoolHub.models.Schedule;
import com.blessedbits.SchoolHub.projections.dto.ScheduleDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ScheduleMapper {
    ScheduleMapper INSTANCE = Mappers.getMapper(ScheduleMapper.class);

    default ScheduleDto toScheduleDto(Schedule schedule, List<String> include) {
        ScheduleDto scheduleDto;
        if (include == null || include.isEmpty()) {
            scheduleDto = BasicDtoMapper.toScheduleDto(schedule);
            return scheduleDto;
        } else {
            scheduleDto = BasicDtoMapper.toBasicScheduleDto(schedule);
        }
        if (include.contains("course")) {
            scheduleDto.setCourse(BasicDtoMapper.toCourseDto(schedule.getCourse()));
        } else {
            scheduleDto.setCourseId(schedule.getCourse().getId());
        }
        if (include.contains("classEntity")) {
            scheduleDto.setSchoolClass(BasicDtoMapper.toClassDto(schedule.getClassEntity()));
        } else {
            scheduleDto.setSchoolClassId(schedule.getClassEntity().getId());
        }
        return scheduleDto;
    }
}
