package com.blessedbits.SchoolHub.repositories;

import com.blessedbits.SchoolHub.models.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SchoolRepository extends JpaRepository<School, Integer> {
    Optional<School> findByName(String name);

    @Query("SELECT s.name AS schoolName, AVG(sub.grade) AS averageGrade " +
            "FROM School s " +
            "JOIN s.users u " +
            "JOIN Submission sub ON sub.student = u " +
            "WHERE sub.grade IS NOT NULL " +
            "GROUP BY s.name " +
            "ORDER BY averageGrade DESC")
    List<Object[]> findSchoolsWithAverageMarks();
}
