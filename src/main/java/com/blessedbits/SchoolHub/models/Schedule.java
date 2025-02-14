package com.blessedbits.SchoolHub.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalTime;

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
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    private ClassEntity classEntity;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
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

    @Column(nullable = false, length = 20)
    private String room;
}
