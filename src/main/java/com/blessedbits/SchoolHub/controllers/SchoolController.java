package com.blessedbits.SchoolHub.controllers;

import com.blessedbits.SchoolHub.dto.CreateSchoolDto;
import com.blessedbits.SchoolHub.misc.CloudFolder;
import com.blessedbits.SchoolHub.models.School;
import com.blessedbits.SchoolHub.repositories.SchoolRepository;
import com.blessedbits.SchoolHub.services.StorageService;
import com.blessedbits.SchoolHub.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.support.NullValue;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/schools")
public class SchoolController {
    private final SchoolRepository schoolRepository;
    private final StorageService storageService;
    private final UserService userService;

    @Autowired
    public SchoolController(SchoolRepository schoolRepository, StorageService storageService, UserService userService) {
        this.schoolRepository = schoolRepository;
        this.storageService = storageService;
        this.userService = userService;
    }

    @GetMapping("/")
    public ResponseEntity<List<School>> getSchools() {
        return new ResponseEntity<>(schoolRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/school")
    public ResponseEntity<School> getSchool(@RequestParam int id) {
        Optional<School> school = schoolRepository.findById(id);
        return school.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/new")
    public ResponseEntity<String> createSchool(@RequestBody CreateSchoolDto schoolDto) {
        School school = new School();
        school.setName(schoolDto.getName());
        school.setAddress(schoolDto.getAddress());
        try {
            schoolRepository.save(school);
            return new ResponseEntity<>(schoolDto.toString(), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/update-info")
    public ResponseEntity<String> updateInfo(@RequestBody CreateSchoolDto schoolDto,
                                             @RequestHeader("Authorization") String authorizationHeader) {
        School school = userService.getUserFromHeader(authorizationHeader).getUserClass().getSchool();
        String name = schoolDto.getName();
        if (name != null && !name.isEmpty()) {
            school.setName(name);
        }
        String address = schoolDto.getAddress();
        if (address != null && !address.isEmpty()) {
            school.setAddress(address);
        }
        try {
            schoolRepository.save(school);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("Data updated", HttpStatus.OK);
    }

    @PostMapping("/update-logo")
    public ResponseEntity<String> updateLogo(@RequestParam MultipartFile logo,
                                             @RequestHeader("Authorization") String authorizationHeader) {
        School school = userService.getUserFromHeader(authorizationHeader).getUserClass().getSchool();
        try {
            String url = storageService.uploadFile(logo, CloudFolder.SCHOOL_IMAGES);
            school.setLogo(url);
            schoolRepository.save(school);
            return new ResponseEntity<>("Image updated", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // For test purposes, needs to change functionality.
    @PostMapping("/add-gallery-image")
    public ResponseEntity<String> addGalleryImage(@RequestParam("file") MultipartFile file) {
        try {
            String url = storageService.uploadFile(file, CloudFolder.SCHOOL_GALLERIES);
            return new ResponseEntity<>("Gallery image added successfully on link: " + url, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
