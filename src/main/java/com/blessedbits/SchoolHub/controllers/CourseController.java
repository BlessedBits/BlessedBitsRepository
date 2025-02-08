package com.blessedbits.SchoolHub.controllers;

import com.blessedbits.SchoolHub.dto.*;
import com.blessedbits.SchoolHub.misc.RoleBasedAccessUtils;
import com.blessedbits.SchoolHub.models.*;
import com.blessedbits.SchoolHub.projections.dto.CourseDto;
import com.blessedbits.SchoolHub.projections.dto.ModuleDto;
import com.blessedbits.SchoolHub.projections.mappers.CourseMapper;
import com.blessedbits.SchoolHub.projections.mappers.ModuleMapper;
import com.blessedbits.SchoolHub.repositories.*;
import com.blessedbits.SchoolHub.services.CourseService;
import com.blessedbits.SchoolHub.services.ModuleService;
import com.blessedbits.SchoolHub.services.SchoolService;
import com.blessedbits.SchoolHub.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
    private final SchoolService schoolService;
    private final ModuleService moduleService;

    @Autowired
    public CourseController(CourseRepository courseRepository,
                            UserService userService, ModuleRepository moduleRepository,
                            MaterialRepository materialRepository, AssignmentRepository assignmentRepository, SubmissionRepository submissionRepository, CourseService courseService, SchoolService schoolService, ModuleService moduleService) {
        this.courseRepository = courseRepository;
        this.userService = userService;
        this.moduleRepository = moduleRepository;
        this.materialRepository = materialRepository;
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;
        this.courseService = courseService;
        this.schoolService = schoolService;
        this.moduleService = moduleService;
    }

    @GetMapping("")
    public ResponseEntity<List<CourseDto>> getCourses(
            @RequestParam(required = false) List<String> include
    ) {
        return new ResponseEntity<>(courseService.getAllAsDto(include), HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<String> createCourse(
            @RequestBody CreateCourseDto courseDto,
            @AuthenticationPrincipal UserEntity user
    ) {
        School school = schoolService.getByIdOrUser(courseDto.getSchoolId(), user);
        if (!RoleBasedAccessUtils.canAccessSchool(user, school)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Course course = new Course();
        course.setName(courseDto.getName());
        course.setSchool(school);
        try {
            courseRepository.save(course);
            return new ResponseEntity<>("Course created", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Unable to create course", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseDto> getCourse(
            @PathVariable Integer id,
            @RequestParam(required = false) List<String> include,
            @AuthenticationPrincipal UserEntity user
    ) {
        Course course = courseService.getLoadedById(id, include);
        if (!RoleBasedAccessUtils.canAccessCourse(user, course)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        CourseDto courseDto = CourseMapper.INSTANCE
                .toCourseDto(course, include);
        return new ResponseEntity<>(courseDto, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateCourse(
            @PathVariable Integer id,
            @RequestBody CourseDto courseDto,
            @AuthenticationPrincipal UserEntity user
    ) {
        Course course = courseService.getById(id);
        if (!RoleBasedAccessUtils.canModifyCourse(user, course)) {
            return new ResponseEntity<>("You can't modify this course", HttpStatus.FORBIDDEN);
        }
        course.setName(courseDto.getName());
        try {
            courseRepository.save(course);
            return new ResponseEntity<>("Course updated", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Unable to update course", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCourse(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserEntity user
    ) {
        Course course = courseService.getById(id);
        if (!RoleBasedAccessUtils.canModifyCourse(user, course)) {
            return new ResponseEntity<>("You can't modify this course", HttpStatus.FORBIDDEN);
        }
        try {
            courseRepository.delete(course);
            return new ResponseEntity<>("Course deleted", HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Unable to delete course", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/modules")
    public ResponseEntity<List<ModuleDto>> getModules(
            @PathVariable Integer id,
            @RequestParam(required = false) List<String> include,
            @AuthenticationPrincipal UserEntity user
    ) {
        Course course = courseService.getById(id);
        if (!RoleBasedAccessUtils.canAccessCourse(user, course)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        List<ModuleDto> modules = moduleService
                .mapAllToDto(courseService.getCourseModulesLoaded(id, include), include);
        return new ResponseEntity<>(modules, HttpStatus.OK);
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
