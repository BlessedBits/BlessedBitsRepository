package com.blessedbits.SchoolHub.models;

import com.blessedbits.SchoolHub.misc.JsonReferenceAsId;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "school_contacts")
@Data
@NoArgsConstructor
public class SchoolContacts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name = "school_id", referencedColumnName = "id")
    @JsonReferenceAsId
    private School school;

    private String phoneNumber;
    private String email;
    private String youtubeLink;
    private String facebookLink;
    private String instagramLink;
    private String tiktokLink;
}
