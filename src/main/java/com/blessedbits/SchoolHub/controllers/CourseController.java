package com.blessedbits.SchoolHub.controllers;

import com.blessedbits.SchoolHub.dto.CreateCourseDto;
import com.blessedbits.SchoolHub.dto.CreateModuleDto;
import com.blessedbits.SchoolHub.models.Course;
import com.blessedbits.SchoolHub.models.ModuleEntity;
import com.blessedbits.SchoolHub.models.UserEntity;
import com.blessedbits.SchoolHub.repositories.CourseRepository;
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

    @Autowired
    public CourseController(CourseRepository courseRepository,
                            UserService userService, ModuleRepository moduleRepository) {
        this.courseRepository = courseRepository;
        this.userService = userService;
        this.moduleRepository = moduleRepository;
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
        course.setSchool(user.getUserClass().getSchool());
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
                .findCourseByNameAndSchoolId(courseName, user.getUserClass().getSchool().getId());
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
}
