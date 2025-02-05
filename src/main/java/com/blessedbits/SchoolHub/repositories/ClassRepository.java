package com.blessedbits.SchoolHub.repositories;

import com.blessedbits.SchoolHub.models.ClassEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassRepository extends JpaRepository<ClassEntity, Integer> 
{
    Optional<ClassEntity> findById(Integer id);
    Optional<ClassEntity> findClassEntityByNameAndSchoolId(String name, Integer schoolId);

    @Query("select c from ClassEntity c")
    <T> List<T> findAll(Class<T> clazz);

    @EntityGraph(attributePaths = {"courses"})
    @Query("select c from ClassEntity c")
    List<ClassEntity> findAllWithCourses();
}

