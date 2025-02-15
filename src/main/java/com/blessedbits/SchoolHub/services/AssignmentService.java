package com.blessedbits.SchoolHub.services;

import com.blessedbits.SchoolHub.misc.EntityManagerUtils;
import com.blessedbits.SchoolHub.models.Assignment;
import com.blessedbits.SchoolHub.models.Submission;
import com.blessedbits.SchoolHub.repositories.AssignmentRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AssignmentService {
    private final AssignmentRepository assignmentRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public AssignmentService(AssignmentRepository assignmentRepository) {
        this.assignmentRepository = assignmentRepository;
    }

    public Assignment getById(Long id) {
        return assignmentRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment with specified id not found"));
    }

    public Assignment getLoadedById(Long id, List<String> include) {
        String jpql = "SELECT a FROM Assignment a WHERE a.id = :id";
        TypedQuery<Assignment> query = EntityManagerUtils
                .createTypedQueryWithGraph(Assignment.class, entityManager, jpql, include);
        query.setParameter("id", id);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment with specified id not found");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return getById(id);
        }
    }

    public List<Submission> getAssignmentSubmissionsLoaded(Long assignmentId, List<String> include) {
        String jpql = "SELECT s FROM Submission s WHERE s.assignment.id = :assignmentId";
        TypedQuery<Submission> query = EntityManagerUtils
                .createTypedQueryWithGraph(Submission.class, entityManager, jpql, include);
        query.setParameter("assignmentId", assignmentId);
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No submissions found for the given assignment");
        } catch (Exception e) {
            System.out.println("Unable to execute query for submissions");
            return getById(assignmentId).getSubmissions().stream().toList();
        }
    }

}
