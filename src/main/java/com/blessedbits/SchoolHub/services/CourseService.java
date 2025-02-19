package com.blessedbits.SchoolHub.services;

import com.blessedbits.SchoolHub.dto.CreateCourseDto;
import com.blessedbits.SchoolHub.misc.EntityManagerUtils;
import com.blessedbits.SchoolHub.misc.RoleBasedAccessUtils;
import com.blessedbits.SchoolHub.models.*;
import com.blessedbits.SchoolHub.projections.dto.CourseDto;
import com.blessedbits.SchoolHub.projections.dto.ModuleDto;
import com.blessedbits.SchoolHub.projections.mappers.CourseMapper;
import com.blessedbits.SchoolHub.repositories.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final ModuleRepository moduleRepository;
    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final SchoolRepository schoolRepository;
    private final UserRepository userRepository;
    private final RoleBasedAccessUtils roleBasedAccessUtils;
    private final ClassRepository classRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public CourseService(UserRepository userRepository, SchoolRepository schoolRepository, CourseRepository courseRepository, ModuleRepository moduleRepository, AssignmentRepository assignmentRepository, SubmissionRepository submissionRepository, RoleBasedAccessUtils roleBasedAccessUtils, ClassRepository classRepository) {
        this.courseRepository = courseRepository;
        this.moduleRepository = moduleRepository;
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;
        this.userRepository = userRepository;
        this.schoolRepository = schoolRepository;
        this.roleBasedAccessUtils = roleBasedAccessUtils;
        this.classRepository = classRepository;
    }

    public Course getById(Integer id) {
        return courseRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course with given id not found")
        );
    }

    public Course getLoadedById(Integer id, List<String> include) {
        String jpql = "SELECT c FROM Course c WHERE c.id = :id";
        TypedQuery<Course> query = EntityManagerUtils
                .createTypedQueryWithGraph(Course.class, entityManager, jpql, include);
        query.setParameter("id", id);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course with given id not found");
        } catch (Exception e) {
            System.out.println("Unable to execute query with entity graph");
            return getById(id);
        }
    }

    public List<ModuleEntity> getCourseModulesLoaded(Integer courseId, List<String> include) {
        String jpql = "SELECT m FROM ModuleEntity m WHERE m.course.id = :courseId";
        TypedQuery<ModuleEntity> query = EntityManagerUtils
                .createTypedQueryWithGraph(ModuleEntity.class, entityManager, jpql, include);
        query.setParameter("courseId", courseId);
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No results found for the query");
        } catch (Exception e) {
            System.out.println("Unable to execute query with entity graph");
            return getById(courseId).getModules().stream().toList();
        }
    }

    public List<ClassEntity> getCourseClassesLoaded(Integer courseId, List<String> include) {
        String jpql = "SELECT c.classes FROM Course c WHERE c.id = :courseId";
        TypedQuery<ClassEntity> query = EntityManagerUtils
                .createTypedQueryWithGraph(ClassEntity.class, entityManager, jpql, include);
        query.setParameter("courseId", courseId);
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No results found for the query");
        } catch (Exception e) {
            System.out.println("Unable to execute query with entity graph");
            return getById(courseId).getClasses().stream().toList();
        }
    }

    public List<UserEntity> getCourseTeachersLoaded(Integer courseId, List<String> include) {
        String jpql = "SELECT c.teachers FROM Course c WHERE c.id = :courseId";
        TypedQuery<UserEntity> query = EntityManagerUtils
                .createTypedQueryWithGraph(UserEntity.class, entityManager, jpql, include);
        query.setParameter("courseId", courseId);
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No results found for the query");
        } catch (Exception e) {
            System.out.println("Unable to execute query with entity graph");
            return getById(courseId).getTeachers().stream().toList();
        }
    }

    public List<Course> getAllLoaded(List<String> include) {
        String jpql = "SELECT c FROM Course c";
        TypedQuery<Course> query = EntityManagerUtils
                .createTypedQueryWithGraph(Course.class, entityManager, jpql, include);
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No results found for the query");
        } catch (Exception e) {
            System.out.println("Unable to execute query with entity graph");
            return courseRepository.findAll();
        }
    }

    public List<CourseDto> getAllAsDto(List<String> include) {
        return getAllLoaded(include).stream()
                .map(course -> CourseMapper.INSTANCE.toCourseDto(course, include))
                .toList();
    }

    public List<CourseDto> mapAllToDto(List<Course> courses, List<String> include) {
        return courses.stream()
                .map(course -> CourseMapper.INSTANCE.toCourseDto(course, include))
                .toList();
    }

    @Transactional
    public void deleteCourseRelations(Course course) {
        for (ClassEntity classEntity : getCourseClassesLoaded(course.getId(), List.of("courses"))) {
            classEntity.getCourses().remove(course);
            classRepository.save(classEntity);
        }
        for (UserEntity teacher : getCourseTeachersLoaded(course.getId(), List.of("teachers"))) {
            teacher.getCourses().remove(course);
            userRepository.save(teacher);
        }
        School school = course.getSchool();
        school.getCourses().remove(course);
        schoolRepository.save(school);
    }

    public ResponseEntity<String> createCourse(CreateCourseDto courseDto, UserEntity user) {
        School school = schoolRepository.findById(courseDto.getSchoolId())
                .orElseGet(() -> user.getSchool());

        if (!roleBasedAccessUtils.canAccessSchool(user, school)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        Set<UserEntity> teachers = new HashSet<>();
        if (courseDto.getTeacherIds() != null) {
            for (Integer teacherId : courseDto.getTeacherIds()) {
                userRepository.findById(teacherId).ifPresent(teachers::add);
            }
        }

        Course course = new Course();
        course.setName(courseDto.getName());
        course.setSchool(school);
        course.setTeachers(teachers);

        try {
            courseRepository.save(course);
            return new ResponseEntity<>("Course created", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Unable to create course", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
