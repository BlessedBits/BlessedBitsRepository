package com.blessedbits.SchoolHub.models;

import com.blessedbits.SchoolHub.misc.JsonReferenceAsId;
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

    @JsonReferenceAsId
    @ManyToOne
    @JoinColumn(name = "school_id")
    private School school;

    @JsonReferenceAsId
    @ManyToMany(mappedBy = "courses")
    private List<ClassEntity> classes;
}
