package com.blessedbits.SchoolHub.controllers;

import com.blessedbits.SchoolHub.dto.AddClassStudentDto;
import com.blessedbits.SchoolHub.dto.AddCourseToClassDto;
import com.blessedbits.SchoolHub.dto.CreateClassDto;
import com.blessedbits.SchoolHub.models.ClassEntity;
import com.blessedbits.SchoolHub.models.Course;
import com.blessedbits.SchoolHub.models.School;
import com.blessedbits.SchoolHub.models.UserEntity;
import com.blessedbits.SchoolHub.repositories.ClassRepository;
import com.blessedbits.SchoolHub.repositories.CourseRepository;
import com.blessedbits.SchoolHub.repositories.UserRepository;
import com.blessedbits.SchoolHub.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/classes")
public class ClassesController {
    private final CourseRepository courseRepository;
    private ClassRepository classRepository;
    private UserRepository userRepository;
    private UserService userService;

    @Autowired
    public ClassesController(ClassRepository classRepository, UserRepository userRepository,
                             UserService userService, CourseRepository courseRepository) {
        this.classRepository = classRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.courseRepository = courseRepository;
    }

    @GetMapping("/")
    public ResponseEntity<List<ClassEntity>> getClasses() {
        return new ResponseEntity<>(classRepository.findAll(), HttpStatus.OK);
    }

    @PostMapping("/new")
    public ResponseEntity<String> createClass(@RequestBody CreateClassDto classDto,
                                              @RequestHeader("Authorization") String authorizationHeader) {
        ClassEntity classEntity = new ClassEntity();
        classEntity.setName(classDto.getName());

        Optional<UserEntity> teacher = userRepository.findByUsername(classDto.getHomeroomTeacher());
        if (teacher.isEmpty()) {
            return new ResponseEntity<>("No teacher found with specified name", HttpStatus.NOT_FOUND);
        }
        classEntity.setHomeroomTeacher(teacher.get());

        School school = userService.getUserFromHeader(authorizationHeader).getSchool();
        classEntity.setSchool(school);

        try {
            classRepository.save(classEntity);
            return new ResponseEntity<>("Class created", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/add-course")
    public ResponseEntity<String> addCourse(
            @RequestBody AddCourseToClassDto addCourseToClassDto,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        UserEntity user = userService.getUserFromHeader(authorizationHeader);
        Optional<Course> courseOptional = courseRepository.findCourseByNameAndSchoolId(
                addCourseToClassDto.getCourseName(), user.getSchool().getId()
        );
        if (courseOptional.isEmpty()) {
            return new ResponseEntity<>("No course found with specified name in your school",
                    HttpStatus.NOT_FOUND);
        }
        Course course = courseOptional.get();
        Optional<ClassEntity> classOptional = classRepository.findClassEntityByNameAndSchoolId(
                addCourseToClassDto.getClassName(), user.getSchool().getId()
        );
        if (classOptional.isEmpty()) {
            return new ResponseEntity<>("No class found with specified name in your school",
                    HttpStatus.NOT_FOUND);
        }
        ClassEntity schoolClass = classOptional.get();
        schoolClass.addCourse(course);
        try {
            classRepository.save(schoolClass);
        } catch (Exception e) {
            return new ResponseEntity<>("Unable to add course to your class",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("Course added to class successfully", HttpStatus.CREATED);
    }

    @PostMapping("/add-student")
    public ResponseEntity<String> addStudent(
            @RequestBody AddClassStudentDto addClassStudentDto,
            @RequestHeader("Authorization") String authorizationHeader) {
        UserEntity user = userService.getUserFromHeader(authorizationHeader);
        Optional<ClassEntity> classOptional = classRepository.findClassEntityByNameAndSchoolId(
                addClassStudentDto.getClassName(), user.getSchool().getId()
        );
        if (classOptional.isEmpty()) {
            return new ResponseEntity<>("No class found with specified name in your school",
                    HttpStatus.NOT_FOUND);
        }
        ClassEntity schoolClass = classOptional.get();
        Optional<UserEntity> studentOptional = userRepository.findByUsername(addClassStudentDto.getUsername());
        if (studentOptional.isEmpty()) {
            return new ResponseEntity<>("No user found with specified username",
                    HttpStatus.NOT_FOUND);
        }
        UserEntity student = studentOptional.get();
        if (student.getSchool().getId() != schoolClass.getSchool().getId()) {
            return new ResponseEntity<>("You are not in the same school", HttpStatus.FORBIDDEN);
        }
        schoolClass.addStudent(student);
        try {
            classRepository.save(schoolClass);
            userRepository.save(student);
            return new ResponseEntity<>("Student added to specified class", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Couldn't add user to specified class",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
