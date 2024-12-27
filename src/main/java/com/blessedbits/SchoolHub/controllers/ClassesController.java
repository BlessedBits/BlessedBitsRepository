package com.blessedbits.SchoolHub.controllers;

import com.blessedbits.SchoolHub.dto.CreateClassDto;
import com.blessedbits.SchoolHub.models.ClassEntity;
import com.blessedbits.SchoolHub.models.School;
import com.blessedbits.SchoolHub.models.UserEntity;
import com.blessedbits.SchoolHub.repositories.ClassRepository;
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
    private ClassRepository classRepository;
    private UserRepository userRepository;
    private UserService userService;

    @Autowired
    public ClassesController(ClassRepository classRepository, UserRepository userRepository,
                             UserService userService) {
        this.classRepository = classRepository;
        this.userRepository = userRepository;
        this.userService = userService;
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

        School school = userService.getUserFromHeader(authorizationHeader).getUserClass().getSchool();
        classEntity.setSchool(school);

        try {
            classRepository.save(classEntity);
            return new ResponseEntity<>("Class created", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
