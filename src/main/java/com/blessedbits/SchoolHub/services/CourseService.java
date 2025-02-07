package com.blessedbits.SchoolHub.services;

import com.blessedbits.SchoolHub.models.*;
import com.blessedbits.SchoolHub.projections.dto.CourseDto;
import com.blessedbits.SchoolHub.projections.mappers.CourseMapper;
import com.blessedbits.SchoolHub.repositories.AssignmentRepository;
import com.blessedbits.SchoolHub.repositories.CourseRepository;
import com.blessedbits.SchoolHub.repositories.ModuleRepository;
import com.blessedbits.SchoolHub.repositories.SubmissionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final ModuleRepository moduleRepository;
    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;

    public CourseService(CourseRepository courseRepository, ModuleRepository moduleRepository, AssignmentRepository assignmentRepository, SubmissionRepository submissionRepository) {
        this.courseRepository = courseRepository;
        this.moduleRepository = moduleRepository;
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;
    }

    public Course getById(Integer id) {
        return courseRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course with given id not found")
        );
    }

    public ModuleEntity getModuleById(Long moduleId) {
        Optional<ModuleEntity> moduleOptional = moduleRepository.findById(moduleId);
        if (moduleOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Specified module does not exist");
        }
        return moduleOptional.get();
    }

    public Assignment getAssignmentById(Long assignmentId) {
        Optional<Assignment> assignmentOptional = assignmentRepository.findById(assignmentId);
        if (assignmentOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Specified assignment does not exist");
        }
        return assignmentOptional.get();
    }

    public Submission getSubmissionById(Long submissionId) {
        Optional<Submission> submissionOptional = submissionRepository.findById(submissionId);
        if (submissionOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Specified submission does not exist");
        }
        return submissionOptional.get();
    }

    public Course getCourseByNameAndSchoolId(String courseName, Integer schoolId) {
        Optional<Course> courseOptional = courseRepository.
                findCourseByNameAndSchoolId(courseName, schoolId);
        if (courseOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Specified course does not exist in your school");
        }
        return courseOptional.get();
    }

    public ModuleEntity getModuleByNameAndCourseId(String moduleName, Integer courseId) {
        Optional<ModuleEntity> moduleOptional = moduleRepository.
                findByNameAndCourseId(moduleName, courseId);
        if (moduleOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Specified module does not exist in this course");
        }
        return moduleOptional.get();
    }

    public Assignment getAssignmentByTitleAndModuleId(String assignmentTitle, Long moduleId) {
        Optional<Assignment> assignmentOptional = assignmentRepository.
                findByTitleAndModuleId(assignmentTitle, moduleId);
        if (assignmentOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Specified assignment does not exist in this module");
        }
        return assignmentOptional.get();
    }

    public Course getCourseByNameOrIdAndSchoolId(Integer courseId, String courseName, Integer schoolId) {
        if (courseId != null) {
            return getById(courseId);
        } else if (courseName != null) {
            return getCourseByNameAndSchoolId(courseName, schoolId);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Either course name or id must be specified");
        }
    }

    public ModuleEntity getModuleByNameOrIdAndCourseId(Long moduleId, String moduleName, Integer courseId) {
        if (moduleId != null) {
            return getModuleById(moduleId);
        } else if (moduleName != null) {
            return getModuleByNameAndCourseId(moduleName, courseId);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Either module name or id must be specified");
        }
    }

    public Assignment getAssignmentByTitleOrIdAndModuleId(
            Long assignmentId, String assignmentTitle, Long moduleId) {
        if (assignmentId != null) {
            return getAssignmentById(assignmentId);
        } else if (assignmentTitle != null) {
            return getAssignmentByTitleAndModuleId(assignmentTitle, moduleId);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Either assignment title or id must be specified");
        }
    }

    public List<CourseDto> getAllAsDto(List<String> include) {
        return courseRepository.findAll().stream()
                .map(course -> CourseMapper.INSTANCE.toCourseDto(course, include))
                .toList();
    }

    public List<CourseDto> mapAllToDto(Set<Course> courses, List<String> include) {
        return courses.stream()
                .map(course -> CourseMapper.INSTANCE.toCourseDto(course, include))
                .toList();
    }

    public List<CourseDto> mapAllToDto(List<Course> courses, List<String> include) {
        return courses.stream()
                .map(course -> CourseMapper.INSTANCE.toCourseDto(course, include))
                .toList();
    }
}
