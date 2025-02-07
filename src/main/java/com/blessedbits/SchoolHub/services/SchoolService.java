package com.blessedbits.SchoolHub.services;

import com.blessedbits.SchoolHub.misc.EntityManagerUtils;
import com.blessedbits.SchoolHub.models.*;
import com.blessedbits.SchoolHub.projections.dto.SchoolDto;
import com.blessedbits.SchoolHub.projections.mappers.SchoolMapper;
import com.blessedbits.SchoolHub.repositories.SchoolRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@Service
public class SchoolService {
    private final SchoolRepository schoolRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public SchoolService(SchoolRepository schoolRepository) {
        this.schoolRepository = schoolRepository;
    }

    public School getById(Integer id) {
        return schoolRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "School with given id not found")
        );
    }

    public School getByIdOrUser(Integer id, UserEntity user) {
        if (id == null) {
            return user.getSchool();
        }
        return getById(id);
    }

    public List<School> getAllLoaded(List<String> include) {
        String jpql = "select s from School s";
        TypedQuery<School> query = EntityManagerUtils
                .createTypedQueryWithGraph(School.class, entityManager, jpql, include);
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No results found for the query");
        } catch (Exception e) {
            System.out.println("Couldn't execute query with entity graph");
            return schoolRepository.findAll();
        }
    }

    public List<UserEntity> getSchoolUsersLoaded(Integer id, List<String> include) {
        String jpql = "select s.users from School s";
        TypedQuery<UserEntity> query = EntityManagerUtils
                .createTypedQueryWithGraph(UserEntity.class, entityManager, jpql, include);
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No results found for the query");
        } catch (Exception e) {
            System.out.println("Couldn't execute query with entity graph");
            return getById(id).getUsers().stream().toList();
        }
    }

    public List<ClassEntity> getSchoolClassesLoaded(Integer id, List<String> include) {
        String jpql = "select s.classes from School s";
        TypedQuery<ClassEntity> query = EntityManagerUtils
                .createTypedQueryWithGraph(ClassEntity.class, entityManager, jpql, include);
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No results found for the query");
        } catch (Exception e) {
            System.out.println("Couldn't execute query with entity graph");
            return getById(id).getClasses().stream().toList();
        }
    }

    public List<Course> getSchoolCoursesLoaded(Integer id, List<String> include) {
        String jpql = "select s.courses from School s";
        TypedQuery<Course> query = EntityManagerUtils
                .createTypedQueryWithGraph(Course.class, entityManager, jpql, include);
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No results found for the query");
        } catch (Exception e) {
            System.out.println("Couldn't execute query with entity graph");
            return getById(id).getCourses().stream().toList();
        }
    }

    public List<News> getSchoolNewsLoaded(Integer id, List<String> include) {
        String jpql = "select s.news from School s";
        TypedQuery<News> query = EntityManagerUtils
                .createTypedQueryWithGraph(News.class, entityManager, jpql, include);
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No results found for the query");
        } catch (Exception e) {
            System.out.println("Couldn't execute query with entity graph");
            return getById(id).getNews().stream().toList();
        }
    }

    public List<SchoolDto> getAllAsDto(List<String> include) {
        return getAllLoaded(include).stream()
                .map(school -> SchoolMapper.INSTANCE.toSchoolDto(school, include))
                .toList();
    }

    public List<SchoolDto> mapAllToDto(Set<School> entities, List<String> include) {
        return entities.stream()
                .map(entity -> SchoolMapper.INSTANCE.toSchoolDto(entity, include))
                .toList();
    }

    public List<SchoolDto> mapAllToDto(List<School> entities, List<String> include) {
        return entities.stream()
                .map(entity -> SchoolMapper.INSTANCE.toSchoolDto(entity, include))
                .toList();
    }
}
