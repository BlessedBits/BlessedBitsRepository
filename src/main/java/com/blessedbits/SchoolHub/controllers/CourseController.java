package com.blessedbits.SchoolHub.controllers;

import com.blessedbits.SchoolHub.dto.CreateCourseDto;
import com.blessedbits.SchoolHub.models.Course;
import com.blessedbits.SchoolHub.models.UserEntity;
import com.blessedbits.SchoolHub.repositories.CourseRepository;
import com.blessedbits.SchoolHub.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseController {
    private CourseRepository courseRepository;
    private UserService userService;

    @Autowired
    public CourseController(CourseRepository courseRepository,
                            UserService userService) {
        this.courseRepository = courseRepository;
        this.userService = userService;
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
}
