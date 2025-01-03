package com.blessedbits.SchoolHub.models;

import com.blessedbits.SchoolHub.misc.JsonReferenceAsId;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "classes")
@Data
@NoArgsConstructor
public class ClassEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;

    private String name;

    @JsonReferenceAsId
    @OneToOne
    @JoinColumn(name = "homeroom_teacher_id")
    private UserEntity homeroomTeacher;

    @JsonReferenceAsId
    @ManyToOne
    @JoinColumn(name = "school_id")
    private School school;

    @JsonReferenceAsId
    @OneToMany(mappedBy = "userClass", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserEntity> students;

    @ManyToMany
    @JoinTable(
            name = "class_courses",
            joinColumns = @JoinColumn(name = "class_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private List<Course> courses;

    public void addCourse(Course course) {
        this.courses.add(course);
        course.getClasses().add(this);
    }

    public void addStudent(UserEntity user) {
        this.students.add(user);
        user.setUserClass(this);
    }
}
