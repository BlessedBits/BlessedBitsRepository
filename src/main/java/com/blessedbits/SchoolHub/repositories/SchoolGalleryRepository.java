package com.blessedbits.SchoolHub.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.blessedbits.SchoolHub.models.School;
import com.blessedbits.SchoolHub.models.SchoolGallery;

@Repository
public interface SchoolGalleryRepository extends JpaRepository<SchoolGallery, Integer> {

    List<SchoolGallery> findBySchool(School school);

    Optional<SchoolGallery> findBySchoolAndGalleryImage(School school, String image);
}

