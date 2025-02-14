package com.blessedbits.SchoolHub.dto;

import java.time.LocalTime;
import lombok.Data;

@Data
public class CreateScheduleDto
{
    private Integer classId;
    private Integer courseId;
    private String dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private String roomNumber;

}
