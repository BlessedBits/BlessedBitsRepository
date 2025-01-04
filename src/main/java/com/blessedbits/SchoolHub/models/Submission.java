package com.blessedbits.SchoolHub.models;

import com.blessedbits.SchoolHub.misc.JsonReferenceAsId;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table
@Data
@NoArgsConstructor
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    private String url;
    private LocalDateTime submittedAt;
    private Integer grade;
    private LocalDateTime gradedAt;

    @JsonReferenceAsId
    @ManyToOne
    @JoinColumn(name = "student_id")
    private UserEntity student;

    @JsonReferenceAsId
    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private UserEntity teacher;

    @JsonReferenceAsId
    @ManyToOne
    @JoinColumn(name = "assignment_id")
    private Assignment assignment;
}
