package com.blessedbits.SchoolHub.misc;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

public class EntityManagerUtils {
    public static <T> EntityGraph<T> createEntityGraph(
            Class<T> entityClass,
            EntityManager entityManager,
            List<String> include
    ) {
        EntityGraph<T> entityGraph = entityManager.createEntityGraph(entityClass);
        if (include != null && !include.isEmpty()) {
            for (String relation : include) {
                try {
                    entityGraph.addAttributeNodes(relation);
                } catch (Exception e) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "One or more specified attributes can't be applied");
                }
            }
        }
        return entityGraph;
    }

    public static <T> TypedQuery<T> createTypedQueryWithGraph(
            Class<T> entityClass,
            EntityManager entityManager,
            String jpql,
            List<String> include
    ) {
        TypedQuery<T> query = entityManager.createQuery(jpql, entityClass);
        query.setHint("jakarta.persistence.fetchgraph", createEntityGraph(entityClass, entityManager, include));
        return query;
    }
}
