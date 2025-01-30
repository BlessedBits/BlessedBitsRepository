package com.blessedbits.SchoolHub.models;

import com.blessedbits.SchoolHub.misc.JsonReferenceAsId;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="users")
@Data
@NoArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    private List<Role> roles = new ArrayList<>();

    @JsonReferenceAsId
    @ManyToOne
    @JoinColumn(name = "class_id")
    private ClassEntity userClass;

    @JsonReferenceAsId
    @ManyToOne
    @JoinColumn(name = "school_id")
    private School school;
}