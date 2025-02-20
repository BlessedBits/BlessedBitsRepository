package com.blessedbits.SchoolHub.services;

import com.blessedbits.SchoolHub.misc.EntityManagerUtils;
import com.blessedbits.SchoolHub.models.Submission;
import com.blessedbits.SchoolHub.projections.dto.SubmissionDto;
import com.blessedbits.SchoolHub.projections.mappers.SubmissionMapper;
import com.blessedbits.SchoolHub.repositories.SubmissionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SubmissionService {
    private final SubmissionRepository submissionRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public SubmissionService(SubmissionRepository submissionRepository) {
        this.submissionRepository = submissionRepository;
    }

    public Submission getById(Long id) {
        return submissionRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Couldn't find submission with specified id"));
    }

    public Submission getLoadedById(Long id, List<String> include) {
        String jpql = "select s from Submission s where s.id = :id";
        TypedQuery<Submission> query = EntityManagerUtils
                .createTypedQueryWithGraph(Submission.class, entityManager, jpql, include);
        query.setParameter("id", id);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Couldn't find submission with specified id");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return getById(id);
        }
    }

    public List<Submission> getLoadedByStudentIdAndDateRange(
            Integer studentId, LocalDateTime startDate, LocalDateTime endDate, List<String> include
    ) {
        String jpql = """
    SELECT sub FROM Submission sub
    WHERE sub.student.id = :studentId
    AND sub.grade IS NOT NULL
    AND sub.gradedAt BETWEEN :startDate AND :endDate
    ORDER BY sub.gradedAt
    """;
        TypedQuery<Submission> query = EntityManagerUtils
                .createTypedQueryWithGraph(Submission.class, entityManager, jpql, include);
        query.setParameter("studentId", studentId);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Couldn't find submission with specified id");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return submissionRepository.findSubmissionsByStudentIdAndDateRange(studentId, startDate, endDate);
        }
    }

    public List<SubmissionDto> mapAllToDto(List<Submission> submissions, List<String> include) {
        return submissions.stream()
                .map(submission -> SubmissionMapper.INSTANCE.toSubmissionDto(submission, include))
                .toList();
    }
}
