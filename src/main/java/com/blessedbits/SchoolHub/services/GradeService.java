package com.blessedbits.SchoolHub.services;

import com.blessedbits.SchoolHub.dto.GradeDto;
import com.blessedbits.SchoolHub.misc.EntityManagerUtils;
import com.blessedbits.SchoolHub.misc.RoleBasedAccessUtils;
import com.blessedbits.SchoolHub.models.Assignment;
import com.blessedbits.SchoolHub.models.Course;
import com.blessedbits.SchoolHub.models.Grade;
import com.blessedbits.SchoolHub.models.UserEntity;
import com.blessedbits.SchoolHub.repositories.AssignmentRepository;
import com.blessedbits.SchoolHub.repositories.CourseRepository;
import com.blessedbits.SchoolHub.repositories.GradeRepository;
import com.blessedbits.SchoolHub.repositories.UserRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GradeService {

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private RoleBasedAccessUtils roleBasedAccessUtils;

    @PersistenceContext
    private EntityManager entityManager;

    public Grade createGrade(GradeDto dto, Long assignmentId, UserEntity teacher) 
    {
        UserEntity student = userRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student with ID " + dto.getStudentId() + " wasn't found"));
        if(!roleBasedAccessUtils.canModifyGrade(teacher, dto))
        {
            throw new IllegalArgumentException("You are not allowed to set grade.");
        }

        Assignment assignment = null;
        Course course = null;

        if (assignmentId != null) {
            assignment = assignmentRepository.findById(assignmentId)
                    .orElseThrow(() -> new RuntimeException("Assignment with ID " + assignmentId + " wasn't found"));
            course = assignment.getModule().getCourse(); 
        } 
        else if (dto.getCourseId() != null) {
            course = courseRepository.findById(dto.getCourseId())
                    .orElseThrow(() -> new RuntimeException("Course with ID " + dto.getCourseId() + " wasn't found"));
        } 
        else {
            throw new RuntimeException("Either assignment ID or course ID must be provided.");
        }

        Grade grade = new Grade();
        grade.setGrade(dto.getGrade());
        grade.setGradedAt(dto.getGradedAt() != null ? dto.getGradedAt() : LocalDateTime.now());
        grade.setType(dto.getType());
        grade.setAssignment(assignment);
        grade.setStudent(student);
        grade.setTeacher(teacher);
        grade.setCourse(course);

        return gradeRepository.save(grade);
    }

    public List<Grade> getFilteredGrades(Integer studentId, Integer classId, Integer courseId, 
                                     LocalDateTime startDate, LocalDateTime endDate, List<String> include) 
{
    String jpql = """
        SELECT g FROM Grade g
        WHERE g.grade IS NOT NULL
        AND g.gradedAt BETWEEN :startDate AND :endDate
        """;

    if (studentId != null) {
        jpql += " AND g.student.id = :studentId";
    }
    if (classId != null) {
        jpql += " AND g.student.userClass.id = :classId";
    }
    if (courseId != null) {
        jpql += " AND g.course.id = :courseId";
    }

    jpql += " ORDER BY g.gradedAt";

    TypedQuery<Grade> query = EntityManagerUtils
            .createTypedQueryWithGraph(Grade.class, entityManager, jpql, include);

    query.setParameter("startDate", startDate);
    query.setParameter("endDate", endDate);

    if (studentId != null) {
        query.setParameter("studentId", studentId);
    }
    if (classId != null) {
        query.setParameter("classId", classId);
    }
    if (courseId != null) {
        query.setParameter("courseId", courseId);
    }

    try {
        return query.getResultList();
    } catch (NoResultException e) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Couldn't find grades for the specified filters");
    } catch (Exception e) {
        return gradeRepository.findByFilters(studentId, classId, courseId, startDate, endDate);
    }
}

    public Grade getGradeById(Long id) {
        return gradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grade with ID " + id + " wasn't found"));
    }

    public Grade updateGrade(Long id, GradeDto dto, UserEntity teacher) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grade with ID " + id + " wasn't found"));
        if(!roleBasedAccessUtils.canModifyGrade(teacher, dto))
        {
            throw new IllegalArgumentException("You are not allowed to update grade.");
        }
        grade.setGrade(dto.getGrade());
        grade.setGradedAt(dto.getGradedAt() != null ? dto.getGradedAt() : LocalDateTime.now());
        grade.setType(dto.getType());

        grade.setTeacher(teacher);
        return gradeRepository.save(grade);
    }

    public GradeDto toDto(Grade grade) {
        if (grade == null) {
            return null;
        }

        GradeDto dto = new GradeDto();
        dto.setGrade(grade.getGrade());
        dto.setType(grade.getType());
        dto.setGradedAt(grade.getGradedAt());

        if (grade.getStudent() != null) {
            dto.setStudentId(grade.getStudent().getId());
        }

        if (grade.getCourse() != null) {
            dto.setCourseId(grade.getCourse().getId());
        }

        return dto;
    }

    public void deleteGrade(Long id, UserEntity teacher) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grade with ID " + id + " wasn't found"));
        if(!roleBasedAccessUtils.canModifyGrade(teacher, toDto(grade)))
        {
            throw new IllegalArgumentException("You are not allowed to delete grade.");
        }
        gradeRepository.deleteById(id);
    }

    public List<Grade> getLoadedByStudentIdAndDateRange(
                    Integer studentId, LocalDateTime startDate, LocalDateTime endDate, List<String> include)
        {
            String jpql = """
                 SELECT g FROM Grade g
                 WHERE g.student.id = :studentId
                 AND g.grade IS NOT NULL
                 AND g.gradedAt BETWEEN :startDate AND :endDate
                 ORDER BY g.gradedAt
                 """;
            TypedQuery<Grade> query = EntityManagerUtils
            .createTypedQueryWithGraph(Grade.class, entityManager, jpql, include);
            query.setParameter("studentId", studentId);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            try {
                return query.getResultList();
            } catch (NoResultException e) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Couldn't find grade with specified id");
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return gradeRepository.findByStudentIdAndDateRange(studentId, startDate, endDate);
            }
        }

}
