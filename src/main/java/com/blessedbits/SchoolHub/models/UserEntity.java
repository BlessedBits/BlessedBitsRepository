package com.blessedbits.SchoolHub.models;

import com.blessedbits.SchoolHub.misc.JsonReferenceAsId;
import com.blessedbits.SchoolHub.misc.RoleType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="users")
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private int id;

    private String firstName;

    private String lastName;

    @Column(unique=true)
    private String username;

    private String password;

    @Column(unique = true)
    private String email;

    private Boolean isConfirmed = false;

    private String profileImage;
    
    private String duty;

    @Enumerated(EnumType.STRING)
    private RoleType role;

    @JsonReferenceAsId
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id")
    private ClassEntity userClass;

    @JsonReferenceAsId
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id")
    private School school;

    @JsonReferenceAsId
    @ManyToMany(mappedBy = "teachers")
    private Set<Course> courses;

    public boolean hasRole(RoleType role) {
        return role.equals(this.role);
    }
}