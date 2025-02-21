package com.blessedbits.SchoolHub.models;

import com.blessedbits.SchoolHub.misc.JsonReferenceAsId;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @EqualsAndHashCode.Include
    private long id;

    private String url;
    private LocalDateTime submittedAt;

    @JsonReferenceAsId
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private UserEntity student;

    @JsonReferenceAsId
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id")
    private Assignment assignment;
}
