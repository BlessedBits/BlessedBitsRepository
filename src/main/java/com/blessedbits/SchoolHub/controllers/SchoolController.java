package com.blessedbits.SchoolHub.controllers;

import com.blessedbits.SchoolHub.dto.AchievementDto;
import com.blessedbits.SchoolHub.dto.AddSchoolUserDto;
import com.blessedbits.SchoolHub.dto.CreateNewsDTO;
import com.blessedbits.SchoolHub.dto.CreateSchoolDto;
import com.blessedbits.SchoolHub.dto.SchoolContactsDto;
import com.blessedbits.SchoolHub.dto.SchoolInfoDto;
import com.blessedbits.SchoolHub.dto.UpdateSchoolInfoDto;
import com.blessedbits.SchoolHub.misc.CloudFolder;
import com.blessedbits.SchoolHub.models.Achievement;
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
    public ResponseEntity<?> getSchool(@RequestHeader("Authorization") String authorizationHeader) {
        Integer schoolId = userService.getUserFromHeader(authorizationHeader).getSchool().getId();
        try{
            return new ResponseEntity<>(schoolService.getSchoolInfo(schoolId), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); 
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); 
        }
    }

    @PostMapping("/new")
    public ResponseEntity<?> createSchool(@RequestBody CreateSchoolDto schoolDto) {
        try {
            School school = schoolService.createSchool(schoolDto);
            return new ResponseEntity<>(school, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update-info")
    public ResponseEntity<String> updateInfo(@RequestBody UpdateSchoolInfoDto schoolDto,
                                             @RequestHeader("Authorization") String authorizationHeader) {
        try{
            School school = userService.getUserFromHeader(authorizationHeader).getSchool();
            schoolService.updateSchoolInfo(school, schoolDto);
            return new ResponseEntity<>("Data updated", HttpStatus.OK);
        }catch(Exception e)
        {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update-logo")
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

    @GetMapping("/contacts")
    public ResponseEntity<?> getSchoolContacts(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            Integer schoolId = userService.getUserFromHeader(authorizationHeader).getSchool().getId();
            SchoolContactsDto schoolContactsDto = schoolService.getSchoolContacts(schoolId);
            return new ResponseEntity<>(schoolContactsDto, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); 
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); 
        }
    }

    @PutMapping("/update-contacts")
    public ResponseEntity<?> updateSchoolContacts(@RequestHeader("Authorization") String authorizationHeader, @RequestBody SchoolContactsDto schoolContactsDto) 
    {
        try{
            Integer schoolId = userService.getUserFromHeader(authorizationHeader).getSchool().getId();
            schoolService.updateSchoolContacts(schoolId, schoolContactsDto);
            return new ResponseEntity<>(schoolContactsDto, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); 
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); 
        }
    }
    
    @GetMapping("/teachers")
    public ResponseEntity<?> getTeachersList(@RequestHeader("Authorization") String authorizationHeader) {
        try{
            Integer schoolId = userService.getUserFromHeader(authorizationHeader).getSchool().getId();
            return new ResponseEntity<>(schoolService.getTeachersBySchool(schoolId), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); 
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); 
        }
        
    }

    @PostMapping("/achievements/create")
    public ResponseEntity<?> createAchievement(@RequestHeader("Authorization") String authorizationHeader, @RequestParam("image") MultipartFile image, @ModelAttribute AchievementDto achievementDto) {
        try{
            Integer schoolId = userService.getUserFromHeader(authorizationHeader).getSchool().getId();
            return new ResponseEntity<>(schoolService.createAchievement(schoolId, image, achievementDto), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); 
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); 
        }
    }
    
    @GetMapping("/achievements")
    public ResponseEntity<?> getSchoolAchievements(@RequestHeader("Authorization") String authorizationHeader) 
    {
        try 
        {
            Integer schoolId = userService.getUserFromHeader(authorizationHeader).getSchool().getId();
            List<Achievement> achievements = schoolService.getAchievementsBySchool(schoolId);
            if(achievements.isEmpty())
            {
                return new ResponseEntity<>("Error: No achievements found for this school.", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(achievements, HttpStatus.OK);
        }catch (Exception e)
        {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PutMapping("/achievements/{id}")
    public ResponseEntity<?> updateAchievement(@RequestHeader("Authorization") String authorizationHeader,
                                            @PathVariable("id") Integer id,
                                            @RequestParam("image") MultipartFile image,
                                            @ModelAttribute AchievementDto achievementDto) {
        try {
            Integer schoolId = userService.getUserFromHeader(authorizationHeader).getSchool().getId();
            Achievement updatedAchievement = schoolService.updateAchievement(schoolId, id, image, achievementDto);
            return new ResponseEntity<>(updatedAchievement, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/achievements/{id}")
    public ResponseEntity<?> deleteAchievement(@RequestHeader("Authorization") String authorizationHeader, @PathVariable("id") Integer id) {
        try {
            Integer schoolId = userService.getUserFromHeader(authorizationHeader).getSchool().getId();
            schoolService.deleteAchievement(schoolId, id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); 
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
