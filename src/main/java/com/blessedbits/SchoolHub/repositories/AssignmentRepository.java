package com.blessedbits.SchoolHub.repositories;

import com.blessedbits.SchoolHub.models.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    Optional<Assignment> findByTitleAndModuleId(String title, Long moduleId);
}
