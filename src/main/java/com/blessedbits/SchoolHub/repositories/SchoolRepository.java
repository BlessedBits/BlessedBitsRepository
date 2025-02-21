package com.blessedbits.SchoolHub.repositories;

import com.blessedbits.SchoolHub.models.School;
import com.blessedbits.SchoolHub.models.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SchoolRepository extends JpaRepository<School, Integer> {
    Optional<School> findByName(String name);

    Optional<School> findById(Integer id);

    @Query("SELECT s.name AS schoolName, AVG(g.grade) AS averageGrade " +
            "FROM School s " +
            "JOIN s.users u " +
            "JOIN Grade g ON g.student = u " +
            "WHERE g.grade IS NOT NULL AND s.id = :id " +
            "GROUP BY s.name " +
            "ORDER BY averageGrade DESC")
    List<Object[]> findSchoolAverageMarks(@Param(value = "id") Integer id);
}
