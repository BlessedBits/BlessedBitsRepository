package com.blessedbits.SchoolHub.repositories;

import com.blessedbits.SchoolHub.models.Grade;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
        
    @Query(value = """
                 SELECT g FROM Grade g
                 WHERE g.student.id = :studentId
                 AND g.grade IS NOT NULL
                 AND g.gradedAt BETWEEN :startDate AND :endDate
                 ORDER BY g.gradedAt
                 """)
    List<Grade> findByStudentIdAndDateRange(
            @Param("studentId") Integer studentId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT g FROM Grade g " +
                "WHERE (:studentId IS NULL OR g.student.id = :studentId) " +
                "AND (:classId IS NULL OR g.student.userClass.id = :classId) " +
                "AND (:courseId IS NULL OR g.course.id = :courseId) " +
                "AND g.gradedAt BETWEEN :startDate AND :endDate " +
                "ORDER BY g.gradedAt")
    List<Grade> findByFilters(@Param("studentId") Integer studentId,
                       @Param("classId") Integer classId,
                       @Param("courseId") Integer courseId,
                       @Param("startDate") LocalDateTime startDate,
                       @Param("endDate") LocalDateTime endDate);

}
