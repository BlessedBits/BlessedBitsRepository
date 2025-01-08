package com.blessedbits.SchoolHub.repositories;

import com.blessedbits.SchoolHub.models.ModuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ModuleRepository extends JpaRepository<ModuleEntity, Long> {
    Optional<ModuleEntity> findByNameAndCourseId(String name, Integer courseId);
}
