package com.blessedbits.SchoolHub.models;

import com.blessedbits.SchoolHub.misc.JsonReferenceAsId;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@Data
@NoArgsConstructor
public class SchoolGallery {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    private String galleryImage;

    @ManyToOne
    @JoinColumn(name = "school_id")
    @JsonReferenceAsId
    private School school;
}
