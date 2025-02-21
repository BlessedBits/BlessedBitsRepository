package com.blessedbits.SchoolHub.models;

import com.blessedbits.SchoolHub.misc.JsonReferenceAsId;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

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
    private Set<ClassEntity> classes;

    @JsonReferenceAsId
    @OneToMany(mappedBy = "school", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Course> courses;

    @JsonReferenceAsId
    @OneToMany(mappedBy = "school", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserEntity> users;

    @JsonReferenceAsId
    @OneToMany(mappedBy = "school", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<News> news;

    @JsonReferenceAsId
    @OneToMany(mappedBy = "school", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Achievement> achievements;

    @JsonReferenceAsId
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "contacts_id", referencedColumnName = "id")
    private SchoolContacts contacts;

    @JsonReferenceAsId
    @OneToMany(mappedBy = "school", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SchoolGallery> gallery;
}
