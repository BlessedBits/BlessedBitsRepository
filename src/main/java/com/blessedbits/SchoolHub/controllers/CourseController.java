package com.blessedbits.SchoolHub.controllers;

import com.blessedbits.SchoolHub.dto.*;
import com.blessedbits.SchoolHub.models.*;
import com.blessedbits.SchoolHub.repositories.*;
import com.blessedbits.SchoolHub.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/courses")
public class CourseController {
    private final CourseRepository courseRepository;
    private final UserService userService;
    private final ModuleRepository moduleRepository;
    private final MaterialRepository materialRepository;
    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;

    @Autowired
    public CourseController(CourseRepository courseRepository,
                            UserService userService, ModuleRepository moduleRepository,
                            MaterialRepository materialRepository, AssignmentRepository assignmentRepository, SubmissionRepository submissionRepository) {
        this.courseRepository = courseRepository;
        this.userService = userService;
        this.moduleRepository = moduleRepository;
        this.materialRepository = materialRepository;
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;
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
    public ResponseEntity<List<Course>> getUserCourses(
            @RequestHeader("Authorization") String authorizationHeader) {
        UserEntity user = userService.getUserFromHeader(authorizationHeader);
        return new ResponseEntity<>(user.getUserClass().getCourses(), HttpStatus.OK);
    }

    @PostMapping("/{courseName}/modules/new")
    public ResponseEntity<String> createModule(
            @PathVariable String courseName,
            @RequestBody CreateModuleDto moduleDto,
            @RequestHeader("Authorization") String authorizationHeader) {
        UserEntity user = userService.getUserFromHeader(authorizationHeader);
        Optional<Course> courseOptional = courseRepository
                .findCourseByNameAndSchoolId(courseName, user.getSchool().getId());
        if (courseOptional.isEmpty()) {
            return new ResponseEntity<>("No course with that name was found in your school",
                    HttpStatus.NOT_FOUND);
        }
        Course course = courseOptional.get();
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

    @PostMapping("/{courseName}/modules/{moduleName}/materials/new")
    public ResponseEntity<String> createMaterial(
            @PathVariable String courseName,
            @PathVariable String moduleName,
            @RequestBody CreateMaterialDto materialDto,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        UserEntity user = userService.getUserFromHeader(authorizationHeader);
        Material material = new Material();
        material.setTitle(materialDto.getTitle());
        material.setDescription(materialDto.getDescription());
        material.setUrl(materialDto.getUrl());
        Optional<ModuleEntity> moduleOptional = moduleRepository.findByNameAndCourseNameAndCourse_SchoolId(
                moduleName, courseName, user.getSchool().getId());
        if (moduleOptional.isEmpty()) {
            return new ResponseEntity<>("Module for given course was not found," +
                    " or course with that name was not found in your school", HttpStatus.NOT_FOUND);
        }
        ModuleEntity module = moduleOptional.get();
        material.setModule(module);
        try {
            materialRepository.save(material);
            return new ResponseEntity<>("Material created", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Couldn't create material", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{courseName}/modules/{moduleName}/assignments/new")
    public ResponseEntity<String> createAssignment(
            @PathVariable String courseName,
            @PathVariable String moduleName,
            @RequestBody CreateAssignmentDto assignmentDto,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        UserEntity user = userService.getUserFromHeader(authorizationHeader);
        Assignment assignment = new Assignment();
        assignment.setTitle(assignmentDto.getTitle());
        assignment.setDescription(assignmentDto.getDescription());
        assignment.setUrl(assignmentDto.getUrl());
        assignment.setDueDate(assignmentDto.getDueDate());
        Optional<ModuleEntity> moduleOptional = moduleRepository.findByNameAndCourseNameAndCourse_SchoolId(
                moduleName, courseName, user.getSchool().getId()
        );
        if (moduleOptional.isEmpty()) {
            return new ResponseEntity<>("Module for given course was not found," +
                    " or course with that name was not found in your school", HttpStatus.NOT_FOUND);
        }
        ModuleEntity module = moduleOptional.get();
        assignment.setModule(module);
        try {
            assignmentRepository.save(assignment);
            return new ResponseEntity<>("Assignment created", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Couldn't create assignment",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{courseName}/modules/{moduleName}/assignments/{assignmentTitle}/new-submission")
    public ResponseEntity<String> createSubmission(
            @PathVariable String courseName,
            @PathVariable String moduleName,
            @PathVariable String assignmentTitle,
            @RequestBody CreateSubmissionDto submissionDto,
            @RequestHeader("Authorization") String authorizationHeader) {
        UserEntity user = userService.getUserFromHeader(authorizationHeader);
        Submission submission = new Submission();
        submission.setUrl(submissionDto.getUrl());
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setStudent(user);
        Optional<ModuleEntity> moduleOptional = moduleRepository.findByNameAndCourseNameAndCourse_SchoolId(
                moduleName, courseName, user.getSchool().getId()
        );
        if (moduleOptional.isEmpty()) {
            return new ResponseEntity<>("Module for given course was not found," +
                    " or course with that name was not found in your school", HttpStatus.NOT_FOUND);
        }
        ModuleEntity module = moduleOptional.get();
        Optional<Assignment> assignmentOptional = assignmentRepository.findByTitleAndModuleId(
                assignmentTitle, module.getId()
        );
        if (assignmentOptional.isEmpty()) {
            return new ResponseEntity<>("Assignment for given course was not found",
                    HttpStatus.NOT_FOUND);
        }
        Assignment assignment = assignmentOptional.get();
        submission.setAssignment(assignment);
        try {
            submissionRepository.save(submission);
            return new ResponseEntity<>("Submission created", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Couldn't create submission",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{courseName}/modules/{moduleName}/assignments/{assignmentTitle}/grade-submission")
    public ResponseEntity<String> gradeSubmission(
            @RequestBody GradeSubmissionDto gradeSubmissionDto,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        UserEntity teacher = userService.getUserFromHeader(authorizationHeader);
        Optional<Submission> submissionOptional = submissionRepository.findById(
                gradeSubmissionDto.getId());
        if (submissionOptional.isEmpty()) {
            return new ResponseEntity<>("Submission not found", HttpStatus.NOT_FOUND);
        }
        Submission submission = submissionOptional.get();
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
