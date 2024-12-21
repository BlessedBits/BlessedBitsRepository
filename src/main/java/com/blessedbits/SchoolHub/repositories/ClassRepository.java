package com.blessedbits.SchoolHub.repositories;

import com.blessedbits.SchoolHub.models.ClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassRepository extends JpaRepository<ClassEntity, Integer> {
}
