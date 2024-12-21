package com.blessedbits.SchoolHub.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table
@Data
@NoArgsConstructor
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "school_id")
    private School school;

    @ManyToMany(mappedBy = "courses")
    private List<ClassEntity> classes;
}
