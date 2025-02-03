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
public class School {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String name;

    private String address;

    private Integer year;

    private String phrase;

    private String logo;

    @JsonReferenceAsId
    @OneToMany(mappedBy = "school", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClassEntity> classes;

    @JsonReferenceAsId
    @OneToMany(mappedBy = "school", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Course> courses;

    @JsonReferenceAsId
    @OneToMany(mappedBy = "school", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserEntity> users;

    @JsonReferenceAsId
    @OneToMany(mappedBy = "school", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<News> news;

    @JsonReferenceAsId
    @OneToMany(mappedBy = "school", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Achievement> achievements;

    @JsonReferenceAsId
    @OneToOne(mappedBy = "school", cascade = CascadeType.ALL, orphanRemoval = true)
    private SchoolContacts contacts;

    @JsonReferenceAsId
    @OneToMany(mappedBy = "school", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SchoolGallery> gallery;
}
