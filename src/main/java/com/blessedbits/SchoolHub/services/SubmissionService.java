package com.blessedbits.SchoolHub.services;

import com.blessedbits.SchoolHub.misc.EntityManagerUtils;
import com.blessedbits.SchoolHub.models.Submission;
import com.blessedbits.SchoolHub.repositories.SubmissionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
}
