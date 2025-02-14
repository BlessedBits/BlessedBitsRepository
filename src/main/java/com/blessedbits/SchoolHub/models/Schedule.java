package com.blessedbits.SchoolHub.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalTime;

import com.blessedbits.SchoolHub.misc.JsonReferenceAsId;

@Entity
@Table
(
    name = "schedules",
    uniqueConstraints = {
        @UniqueConstraint(
            columnNames = {"class_id", "day_of_week", "start_time", "end_time"}
        ), 
        @UniqueConstraint(
            columnNames = {"room_number", "day_of_week", "start_time", "end_time"}
        ) 
    }
)
@Data
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    @JsonReferenceAsId
    private ClassEntity classEntity;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    @JsonReferenceAsId
    private Course course;

    public static enum DayOfWeek {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(name = "room_number", nullable = false, length = 20)
    private String roomNumber;    
}
