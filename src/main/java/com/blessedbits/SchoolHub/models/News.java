package com.blessedbits.SchoolHub.models;

import java.time.LocalDateTime;

import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.URL;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "news")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class News 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank
    private String title;

    @URL
    private String newsImage;

    @URL
    private String link;

    @NotBlank
    private String category;

    @ManyToOne(fetch = FetchType.EAGER, optional = false) 
    @JoinColumn(name = "school_id", nullable = false) 
    @NotNull
    private School school;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
