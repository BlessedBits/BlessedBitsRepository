package com.blessedbits.SchoolHub.models;

import com.blessedbits.SchoolHub.misc.JsonReferenceAsId;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(
        name = "classes",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"name", "school_id"}),
                @UniqueConstraint(columnNames = {"homeroom_teacher_id", "school_id"})
        }
)
@Data
@NoArgsConstructor
public class ClassEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;

    @Column(nullable = false)
    private String name;

    @JsonReferenceAsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "homeroom_teacher_id")
    private UserEntity homeroomTeacher;

    @JsonReferenceAsId
    @ManyToOne(fetch = FetchType.LAZY)
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

    public void removeCourse(Course course) {
        this.courses.remove(course);
        course.getClasses().remove(this);
    }

    public void addStudent(UserEntity user) {
        this.students.add(user);
        user.setUserClass(this);
    }

    public void removeStudent(UserEntity user) {
        this.students.remove(user);
        user.setUserClass(null);
    }
}
