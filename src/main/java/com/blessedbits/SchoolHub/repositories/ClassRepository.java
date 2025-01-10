package com.blessedbits.SchoolHub.repositories;

import com.blessedbits.SchoolHub.models.ClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClassRepository extends JpaRepository<ClassEntity, Integer> 
{
    Optional<ClassEntity> findById(Integer id);
    Optional<ClassEntity> findClassEntityByNameAndSchoolId(String name, Integer schoolId);
}

