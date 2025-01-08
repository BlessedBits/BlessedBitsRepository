package com.blessedbits.SchoolHub.models;

import com.blessedbits.SchoolHub.misc.JsonReferenceAsId;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"title", "module_id"})
        }
)
@Data
@NoArgsConstructor
public class Material {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(nullable = false)
    private String title;
    private String description;
    private String url;

    @JsonReferenceAsId
    @ManyToOne
    @JoinColumn(name = "module_id")
    private ModuleEntity module;
}
