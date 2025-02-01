package com.blessedbits.SchoolHub.models;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@Data
@NoArgsConstructor
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;

    @Column(nullable = false)
    private String title;

    private String description;

    private String image;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "school_id", nullable = false)
    private School school;
}
