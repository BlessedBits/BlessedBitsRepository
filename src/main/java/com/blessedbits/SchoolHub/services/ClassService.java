package com.blessedbits.SchoolHub.services;

import com.blessedbits.SchoolHub.misc.EntityManagerUtils;
import com.blessedbits.SchoolHub.misc.RoleBasedAccessUtils;
import com.blessedbits.SchoolHub.models.ClassEntity;
import com.blessedbits.SchoolHub.models.Course;
import com.blessedbits.SchoolHub.models.Schedule;
import com.blessedbits.SchoolHub.models.UserEntity;
import com.blessedbits.SchoolHub.projections.dto.ClassDto;
import com.blessedbits.SchoolHub.projections.mappers.ClassMapper;
import com.blessedbits.SchoolHub.repositories.ClassRepository;
import com.blessedbits.SchoolHub.repositories.TeacherCourseClassRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@Service
public class ClassService {
    private final ClassRepository classRepository;
    private final TeacherCourseClassRepository teacherCourseClassRepository;
    private final RoleBasedAccessUtils roleBasedAccessUtils;
    private final UserService userService;
    private final CourseService courseService;

    @PersistenceContext
    private EntityManager entityManager;

    public ClassService(ClassRepository classRepository, 
                        RoleBasedAccessUtils roleBasedAccessUtils, 
                        TeacherCourseClassRepository teacherCourseClassRepository, 
                        UserService userService,
                        CourseService courseService) {
        this.classRepository = classRepository;
        this.teacherCourseClassRepository = teacherCourseClassRepository;
        this.roleBasedAccessUtils = roleBasedAccessUtils;
        this.userService = userService;
        this.courseService = courseService;
    }

    public ClassEntity getById(Integer id) {
        return classRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Class with given id not found")
        );
    }

    public ClassEntity getLoadedById(Integer id, List<String> include) {
        String jpql = "select c from ClassEntity c where c.id = :id";
        TypedQuery<ClassEntity> query = EntityManagerUtils
                .createTypedQueryWithGraph(ClassEntity.class, entityManager, jpql, include);
        query.setParameter("id", id);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Class with given id not found", e);
        } catch (Exception e) {
            System.out.println("Couldn't execute query with entity graph");
            return getById(id);
        }
    }

    public List<ClassEntity> getAllLoaded(List<String> include) {
        String jpql = "select c from ClassEntity c";
        TypedQuery<ClassEntity> query = EntityManagerUtils
                .createTypedQueryWithGraph(ClassEntity.class, entityManager, jpql, include);
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No results found for the query", e);
        } catch (Exception e) {
            System.out.println("Couldn't execute query with entity graph");
            return classRepository.findAll();
        }
    }

    public List<Course> getClassCoursesLoaded(Integer id, List<String> include) {
        String jpql = "select cl.courses from ClassEntity cl where cl.id = :id";
        TypedQuery<Course> query = EntityManagerUtils
                .createTypedQueryWithGraph(Course.class, entityManager, jpql, include);
        query.setParameter("id", id);
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No results found for the query", e);
        } catch (Exception e) {
            System.out.println("Couldn't execute query with entity graph");
            return getById(id).getCourses().stream().toList();
        }
    }

    public List<UserEntity> getClassStudentsLoaded(Integer id, List<String> include) {
        String jpql = "select cl.students from ClassEntity cl where cl.id = :id";
        TypedQuery<UserEntity> query = EntityManagerUtils
                .createTypedQueryWithGraph(UserEntity.class, entityManager, jpql, include);
        query.setParameter("id", id);
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No results found for the query", e);
        } catch (Exception e) {
            System.out.println("Couldn't execute query with entity graph");
            return getById(id).getStudents().stream().toList();
        }
    }

    public List<Schedule> getClassSchedulesLoaded(Integer id, List<String> include) {
        String jpql = "select cl.schedules from ClassEntity cl where cl.id = :id";
        TypedQuery<Schedule> query = EntityManagerUtils
                .createTypedQueryWithGraph(Schedule.class, entityManager, jpql, include);
        query.setParameter("id", id);
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No results found for the query", e);
        } catch (Exception e) {
            System.out.println("Couldn't execute query with entity graph");
            return getById(id).getSchedules().stream().toList();
        }
    }

    public List<ClassDto> getAllAsDto(List<String> include) {
        return getAllLoaded(include).stream()
                .map(classEntity -> ClassMapper.INSTANCE.toClassDto(classEntity, include))
                .toList();
    }

    public List<ClassDto> mapAllToDto(Set<ClassEntity> entities, List<String> include) {
        return entities.stream()
                .map(classEntity -> ClassMapper.INSTANCE.toClassDto(classEntity, include))
                .toList();
    }

    public List<ClassDto> mapAllToDto(List<ClassEntity> entities, List<String> include) {
        return entities.stream()
                .map(classEntity -> ClassMapper.INSTANCE.toClassDto(classEntity, include))
                .toList();
    }

    @Transactional
    public void deleteTeacherFromClassCourse(UserEntity user, Integer classId, Integer courseId, Integer teacherId) {
        Course course = courseService.getById(courseId);
        ClassEntity classEntity = getById(classId);
        UserEntity teacher = userService.getById(teacherId);
    
        if (!roleBasedAccessUtils.canModifyCourse(user, course)) {
            throw new AccessDeniedException("You can't modify this course");
        }
        if (!roleBasedAccessUtils.canModifyClass(user, classEntity)) {
            throw new AccessDeniedException("You can't modify this class");
        }
        if (!classEntity.getSchool().equals(course.getSchool())) {
            throw new IllegalArgumentException("Class and course belong to different schools");
        }
        if (!course.getTeachers().contains(teacher)) {
            throw new IllegalArgumentException("This teacher is not assigned to this course");
        }
        teacherCourseClassRepository.deleteByClassEntityIdAndCourseIdAndTeacherId(classId, courseId, teacherId);
    }
}
