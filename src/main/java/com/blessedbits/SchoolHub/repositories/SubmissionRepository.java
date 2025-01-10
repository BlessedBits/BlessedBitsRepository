package com.blessedbits.SchoolHub.repositories;

import com.blessedbits.SchoolHub.models.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    @Query(value = """
            SELECT sub FROM Submission sub
            WHERE sub.student.id=:studentId AND sub.grade IS NOT NULL
            AND sub.gradedAt BETWEEN :startDate AND :endDate
            ORDER BY sub.gradedAt
""")
    List<Submission> findSubmissionsByStudentIdAndDateRange(
            @Param("studentId") Integer studentId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
