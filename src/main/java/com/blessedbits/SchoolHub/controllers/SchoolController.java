package com.blessedbits.SchoolHub.controllers;

import com.blessedbits.SchoolHub.dto.CreateSchoolDto;
import com.blessedbits.SchoolHub.misc.CloudFolder;
import com.blessedbits.SchoolHub.models.School;
import com.blessedbits.SchoolHub.repositories.SchoolRepository;
import com.blessedbits.SchoolHub.services.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.support.NullValue;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/schools")
public class SchoolController {
    private final SchoolRepository schoolRepository;
    private final StorageService storageService;

    @Autowired
    public SchoolController(SchoolRepository schoolRepository, StorageService storageService) {
        this.schoolRepository = schoolRepository;
        this.storageService = storageService;
    }

    @GetMapping("/")
    public ResponseEntity<List<School>> getSchools() {
        return new ResponseEntity<>(schoolRepository.findAll(), HttpStatus.OK);
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

    // For test purposes, needs to change functionality.
    @PostMapping("/addGalleryImage")
    public ResponseEntity<String> addGalleryImage(@RequestParam("file") MultipartFile file) {
        try {
            String url = storageService.uploadFile(file, CloudFolder.SCHOOL_GALLERIES);
            return new ResponseEntity<>("Gallery image added successfully on link: " + url, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
