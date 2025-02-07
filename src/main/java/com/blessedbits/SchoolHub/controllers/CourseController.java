package com.blessedbits.SchoolHub.controllers;

import com.blessedbits.SchoolHub.dto.*;
import com.blessedbits.SchoolHub.models.*;
import com.blessedbits.SchoolHub.repositories.*;
import com.blessedbits.SchoolHub.services.CourseService;
import com.blessedbits.SchoolHub.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/courses")
public class CourseController {
    private final CourseRepository courseRepository;
    private final UserService userService;
    private final ModuleRepository moduleRepository;
    private final MaterialRepository materialRepository;
    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final CourseService courseService;

    @Autowired
    public CourseController(CourseRepository courseRepository,
                            UserService userService, ModuleRepository moduleRepository,
                            MaterialRepository materialRepository, AssignmentRepository assignmentRepository, SubmissionRepository submissionRepository, CourseService courseService) {
        this.courseRepository = courseRepository;
        this.userService = userService;
        this.moduleRepository = moduleRepository;
        this.materialRepository = materialRepository;
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;
        this.courseService = courseService;
    }

    @GetMapping("/")
    public ResponseEntity<List<Course>> getCourses() {
        return new ResponseEntity<>(courseRepository.findAll(), HttpStatus.OK);
    }

    @PostMapping("/new")
    public ResponseEntity<String> createCourse(
            @RequestBody CreateCourseDto courseDto,
            @RequestHeader("Authorization") String authorizationHeader) {
        UserEntity user = userService.getUserFromHeader(authorizationHeader);
        Course course = new Course();
        course.setName(courseDto.getName());
        course.setSchool(user.getSchool());
        try {
            courseRepository.save(course);
            return new ResponseEntity<>("Course created", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Couldn't create course", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/user")
    public ResponseEntity<Set<Course>> getUserCourses(
            @RequestHeader("Authorization") String authorizationHeader) {
        UserEntity user = userService.getUserFromHeader(authorizationHeader);
        return new ResponseEntity<>(user.getUserClass().getCourses(), HttpStatus.OK);
    }

    @PostMapping("/modules")
    public ResponseEntity<String> createModule(
            @RequestParam(required = false) Integer courseId,
            @RequestParam(required = false) String courseName,
            @RequestBody CreateModuleDto moduleDto,
            @RequestHeader("Authorization") String authorizationHeader) {
        UserEntity user = userService.getUserFromHeader(authorizationHeader);

        Course course = courseService.
                getCourseByNameOrIdAndSchoolId(courseId, courseName, user.getSchool().getId());
        if (course.getSchool() != user.getSchool()) {
            return new ResponseEntity<>("You are not allowed to modify this course",
                    HttpStatus.FORBIDDEN);
        }

        ModuleEntity module = new ModuleEntity();
        module.setName(moduleDto.getName());
        module.setCourse(course);
        try {
            moduleRepository.save(module);
            return new ResponseEntity<>("Module created", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Couldn't create module", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/modules/materials")
    public ResponseEntity<String> createMaterial(
            @RequestParam(required = false) Integer courseId,
            @RequestParam(required = false) String courseName,
            @RequestParam(required = false) Long moduleId,
            @RequestParam(required = false) String moduleName,
            @RequestBody CreateMaterialDto materialDto,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        UserEntity user = userService.getUserFromHeader(authorizationHeader);

        Course course = courseService.
                getCourseByNameOrIdAndSchoolId(courseId, courseName, user.getSchool().getId());
        if (course.getSchool() != user.getSchool()) {
            return new ResponseEntity<>("You are not allowed to modify this course",
                    HttpStatus.FORBIDDEN);
        }

        ModuleEntity module = courseService.
                getModuleByNameOrIdAndCourseId(moduleId, moduleName, course.getId());

        Material material = new Material();
        material.setTitle(materialDto.getTitle());
        material.setDescription(materialDto.getDescription());
        material.setUrl(materialDto.getUrl());
        material.setModule(module);
        try {
            materialRepository.save(material);
            return new ResponseEntity<>("Material created", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Couldn't create material", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/modules/assignments/")
    public ResponseEntity<String> createAssignment(
            @RequestParam(required = false) Integer courseId,
            @RequestParam(required = false) String courseName,
            @RequestParam(required = false) Long moduleId,
            @RequestParam(required = false) String moduleName,
            @RequestBody CreateAssignmentDto assignmentDto,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        UserEntity user = userService.getUserFromHeader(authorizationHeader);

        Course course = courseService.
                getCourseByNameOrIdAndSchoolId(courseId, courseName, user.getSchool().getId());
        if (course.getSchool() != user.getSchool()) {
            return new ResponseEntity<>("You are not allowed to modify this course",
                    HttpStatus.FORBIDDEN);
        }

        ModuleEntity module = courseService.
                getModuleByNameOrIdAndCourseId(moduleId, moduleName, course.getId());

        Assignment assignment = new Assignment();
        assignment.setTitle(assignmentDto.getTitle());
        assignment.setDescription(assignmentDto.getDescription());
        assignment.setUrl(assignmentDto.getUrl());
        assignment.setDueDate(assignmentDto.getDueDate());
        assignment.setModule(module);
        try {
            assignmentRepository.save(assignment);
            return new ResponseEntity<>("Assignment created", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Couldn't create assignment",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/modules/assignments/submissions")
    public ResponseEntity<String> createSubmission(
            @RequestParam(required = false) Integer courseId,
            @RequestParam(required = false) String courseName,
            @RequestParam(required = false) Long moduleId,
            @RequestParam(required = false) String moduleName,
            @RequestParam(required = false) Long assignmentId,
            @RequestParam(required = false) String assignmentTitle,
            @RequestBody CreateSubmissionDto submissionDto,
            @RequestHeader("Authorization") String authorizationHeader) {
        UserEntity user = userService.getUserFromHeader(authorizationHeader);

        Course course = courseService.
                getCourseByNameOrIdAndSchoolId(courseId, courseName, user.getSchool().getId());
        if (course.getSchool() != user.getSchool()) {
            return new ResponseEntity<>("You are not allowed to modify this course",
                    HttpStatus.FORBIDDEN);
        }

        ModuleEntity module = courseService.
                getModuleByNameOrIdAndCourseId(moduleId, moduleName, course.getId());

        Assignment assignment = courseService.
                getAssignmentByTitleOrIdAndModuleId(assignmentId, assignmentTitle, module.getId());

        Submission submission = new Submission();
        submission.setUrl(submissionDto.getUrl());
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setStudent(user);
        submission.setAssignment(assignment);
        try {
            submissionRepository.save(submission);
            return new ResponseEntity<>("Submission created", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Couldn't create submission",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/modules/assignments/submissions/grade")
    public ResponseEntity<String> gradeSubmission(
            @RequestBody GradeSubmissionDto gradeSubmissionDto,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        UserEntity teacher = userService.getUserFromHeader(authorizationHeader);

        Submission submission = courseService.getSubmissionById(gradeSubmissionDto.getSubmissionId());
        if (submission.getStudent().getSchool() != teacher.getSchool()) {
            return new ResponseEntity<>("You are not allowed to grade this submission",
                    HttpStatus.FORBIDDEN);
        }

        submission.setGrade(gradeSubmissionDto.getGrade());
        submission.setTeacher(teacher);
        submission.setGradedAt(LocalDateTime.now());
        try {
            submissionRepository.save(submission);
            return new ResponseEntity<>("Submission created", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Couldn't grade submission", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
