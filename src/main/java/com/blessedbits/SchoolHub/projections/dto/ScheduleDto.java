package com.blessedbits.SchoolHub.projections.dto;

import com.blessedbits.SchoolHub.models.Schedule;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalTime;

@Data
public class ScheduleDto {
    private Integer id;
    private Schedule.DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private String room;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer schoolClassId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ClassDto schoolClass;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer courseId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private CourseDto course;
}