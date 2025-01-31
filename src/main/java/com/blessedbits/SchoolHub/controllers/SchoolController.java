package com.blessedbits.SchoolHub.controllers;

import com.blessedbits.SchoolHub.dto.AddSchoolUserDto;
import com.blessedbits.SchoolHub.dto.CreateSchoolDto;
import com.blessedbits.SchoolHub.dto.SchoolContactsDto;
import com.blessedbits.SchoolHub.dto.SchoolInfoDto;
import com.blessedbits.SchoolHub.misc.CloudFolder;
import com.blessedbits.SchoolHub.models.School;
import com.blessedbits.SchoolHub.models.SchoolContacts;
import com.blessedbits.SchoolHub.models.UserEntity;
import com.blessedbits.SchoolHub.repositories.SchoolRepository;
import com.blessedbits.SchoolHub.repositories.UserRepository;
import com.blessedbits.SchoolHub.services.StorageService;
import com.blessedbits.SchoolHub.services.UserService;
import com.blessedbits.SchoolHub.services.SchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;




@RestController
@RequestMapping("/schools")
public class SchoolController {
    private final SchoolRepository schoolRepository;
    private final StorageService storageService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final SchoolService schoolService;

    @Autowired
    public SchoolController(SchoolService schoolService, SchoolRepository schoolRepository, StorageService storageService, UserService userService, UserRepository userRepository) {
        this.schoolRepository = schoolRepository;
        this.storageService = storageService;
        this.userService = userService;
        this.userRepository = userRepository;
        this.schoolService = schoolService;
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
        SchoolContacts contacts = new SchoolContacts();
        contacts.setSchool(school);
        school.setContacts(contacts);
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
        School school = userService.getUserFromHeader(authorizationHeader).getSchool();
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
        School school = userService.getUserFromHeader(authorizationHeader).getSchool();
        try {
            String url = storageService.uploadFile(logo, CloudFolder.SCHOOL_IMAGES);
            school.setLogo(url);
            schoolRepository.save(school);
            return new ResponseEntity<>("Image updated", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/add-user")
    public ResponseEntity<String> addUser(@RequestBody AddSchoolUserDto addSchoolUserDto,
                                          @RequestHeader("Authorization") String authorizationHeader) {
        UserEntity user = userService.getUserFromHeader(authorizationHeader);
        School school;
        String schoolName = addSchoolUserDto.getSchoolName();
        if (schoolName == null || schoolName.isEmpty()) {
            school = user.getSchool();
        } else {
            Optional<School> schoolOptional = schoolRepository.findByName(schoolName);
            if (schoolOptional.isEmpty()) {
                return new ResponseEntity<>("School with specified name not found",
                        HttpStatus.NOT_FOUND);
            }
            school = schoolOptional.get();
        }
        user.setSchool(school);
        try {
            userRepository.save(user);
            return new ResponseEntity<>("User added", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Couldn't add user to specified school",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/rating")
    public ResponseEntity<List<Map<String, Object>>> getRating() {
        List<Object[]> results = schoolRepository.findSchoolsWithAverageMarks();
        return new ResponseEntity<>(
                results.stream().map(row -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("schoolName", row[0]);
                    map.put("averageGrade", row[1]);
                    return map;
                }).collect(Collectors.toList()), HttpStatus.OK
        );
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

    @GetMapping("/{id}/contacts")
    public ResponseEntity<?> getSchoolContacts(@PathVariable Integer id) {
        try {
            SchoolContactsDto schoolContactsDto = schoolService.getSchoolContacts(id);
            return new ResponseEntity<>(schoolContactsDto, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); 
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); 
        }
    }

    @PutMapping("/{id}/update-contacts")
    public ResponseEntity<?> updateSchoolContacts(@PathVariable Integer id, @RequestBody SchoolContactsDto schoolContactsDto) 
    {
        try{
            schoolService.updateSchoolContacts(id, schoolContactsDto);
            return new ResponseEntity<>(schoolContactsDto, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); 
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); 
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getSchoolInfo(@PathVariable Integer id) {
        try{
            return new ResponseEntity<>(schoolService.getSchoolInfo(id), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); 
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); 
        }
        
    }
    
    @GetMapping("/{id}/teachers")
    public ResponseEntity<?> getTeachersList(@PathVariable Integer id) {
        try{
            return new ResponseEntity<>(schoolService.getTeachersBySchool(id), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); 
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); 
        }
        
    }

    @GetMapping("/teachers/{id}")
    public ResponseEntity<?> getTeacherInfo(@PathVariable Integer id) {
        try{
            return new ResponseEntity<>(schoolService.getTeacherInfo(id), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); 
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); 
        }
    }
    

}
