package com.blessedbits.SchoolHub.controllers;

import com.blessedbits.SchoolHub.dto.CreateAssignmentDto;
import com.blessedbits.SchoolHub.dto.CreateCourseDto;
import com.blessedbits.SchoolHub.dto.CreateMaterialDto;
import com.blessedbits.SchoolHub.dto.CreateModuleDto;
import com.blessedbits.SchoolHub.models.*;
import com.blessedbits.SchoolHub.repositories.AssignmentRepository;
import com.blessedbits.SchoolHub.repositories.CourseRepository;
import com.blessedbits.SchoolHub.repositories.MaterialRepository;
import com.blessedbits.SchoolHub.repositories.ModuleRepository;
import com.blessedbits.SchoolHub.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    public CourseController(CourseRepository courseRepository,
                            UserService userService, ModuleRepository moduleRepository,
                            MaterialRepository materialRepository, AssignmentRepository assignmentRepository) {
        this.courseRepository = courseRepository;
        this.userService = userService;
        this.moduleRepository = moduleRepository;
        this.materialRepository = materialRepository;
        this.assignmentRepository = assignmentRepository;
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

    @PostMapping("/{courseName}/modules/{moduleName}/new-material")
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

    @PostMapping("/{courseName}/modules/{moduleName}/new-assignment")
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

}
