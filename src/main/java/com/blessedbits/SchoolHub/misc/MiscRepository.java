package com.blessedbits.SchoolHub.misc;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Component;

@Component
public class MiscRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public Boolean classHasCourse(Integer classId, Integer courseId) {
        String sql = "SELECT 1 FROM class_courses WHERE class_id = :classId AND course_id = :courseId";
        Query query = entityManager.createNativeQuery(sql, Boolean.class);
        query.setParameter("classId", classId);
        query.setParameter("courseId", courseId);
        return !query.getResultList().isEmpty();
    }
}
