package com.blessedbits.SchoolHub.models;

import com.blessedbits.SchoolHub.misc.JsonReferenceAsId;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "school_contacts")
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SchoolContacts {
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(mappedBy = "contacts")
    @JsonReferenceAsId
    private School school;

    private String phoneNumber;
    private String email;
    private String youtubeLink;
    private String facebookLink;
    private String instagramLink;
    private String tiktokLink;
}
