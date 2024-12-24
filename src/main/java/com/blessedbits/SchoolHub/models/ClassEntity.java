package com.blessedbits.SchoolHub.models;

import com.blessedbits.SchoolHub.misc.JsonReferenceAsId;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
}
