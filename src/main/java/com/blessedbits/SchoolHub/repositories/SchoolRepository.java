package com.blessedbits.SchoolHub.repositories;

import com.blessedbits.SchoolHub.models.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SchoolRepository extends JpaRepository<School, Integer> {
    Optional<School> findByName(String name);
}
