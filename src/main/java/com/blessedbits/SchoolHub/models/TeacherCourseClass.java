package com.blessedbits.SchoolHub.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "teacher_course_class",
       uniqueConstraints = @UniqueConstraint(columnNames = {"teacher_id", "course_id", "class_id"}))
@Data
@NoArgsConstructor
public class TeacherCourseClass {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    private UserEntity teacher;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    private ClassEntity classEntity;

}
