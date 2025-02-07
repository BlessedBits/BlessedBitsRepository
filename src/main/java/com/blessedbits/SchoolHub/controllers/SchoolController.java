package com.blessedbits.SchoolHub.controllers;

import com.blessedbits.SchoolHub.dto.AddSchoolUserDto;
import com.blessedbits.SchoolHub.dto.CreateSchoolDto;
import com.blessedbits.SchoolHub.misc.CloudFolder;
import com.blessedbits.SchoolHub.misc.RoleBasedAccessUtils;
import com.blessedbits.SchoolHub.models.News;
import com.blessedbits.SchoolHub.models.School;
import com.blessedbits.SchoolHub.models.UserEntity;
import com.blessedbits.SchoolHub.projections.dto.ClassDto;
import com.blessedbits.SchoolHub.projections.dto.CourseDto;
import com.blessedbits.SchoolHub.projections.dto.SchoolDto;
import com.blessedbits.SchoolHub.projections.dto.UserDto;
import com.blessedbits.SchoolHub.projections.mappers.BasicDtoMapper;
import com.blessedbits.SchoolHub.projections.mappers.SchoolMapper;
import com.blessedbits.SchoolHub.repositories.SchoolRepository;
import com.blessedbits.SchoolHub.repositories.UserRepository;
import com.blessedbits.SchoolHub.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final ClassService classService;
    private final CourseService courseService;

    @Autowired
    public SchoolController(SchoolRepository schoolRepository, StorageService storageService, UserService userService, UserRepository userRepository, SchoolService schoolService, ClassService classService, CourseService courseService) {
        this.schoolRepository = schoolRepository;
        this.storageService = storageService;
        this.userService = userService;
        this.userRepository = userRepository;
        this.schoolService = schoolService;
        this.classService = classService;
        this.courseService = courseService;
    }

    @GetMapping("")
    public ResponseEntity<List<SchoolDto>> getSchools(
            @RequestParam(required = false) List<String> include
    ) {
        return new ResponseEntity<>(schoolService.getAllAsDto(include), HttpStatus.OK);
    }

    @PostMapping("")
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

    @GetMapping("/{id}")
    public ResponseEntity<SchoolDto> getSchool(
            @PathVariable Integer id,
            @RequestParam(required = false) List<String> include,
            @AuthenticationPrincipal UserEntity user
    ) {
        School school = schoolService.getById(id);
        SchoolDto schoolDto;
        if (RoleBasedAccessUtils.canAccessSchool(user, school)) {
            schoolDto = SchoolMapper.INSTANCE.toSchoolDto(school, include);
        } else {
            schoolDto = BasicDtoMapper.toSchoolDto(school);
        }
        return new ResponseEntity<>(schoolDto, HttpStatus.OK);
    }

    @PutMapping("/{id}/info")
    public ResponseEntity<String> updateInfo(
            @PathVariable Integer id,
            @RequestBody CreateSchoolDto schoolDto,
            @AuthenticationPrincipal UserEntity user) {
        School school = schoolService.getByIdOrUser(id, user);
        if (!RoleBasedAccessUtils.canModifySchool(user, school)) {
            return new ResponseEntity<>("You can't modify this school", HttpStatus.FORBIDDEN);
        }
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
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Unable to update info", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("Data updated", HttpStatus.OK);
    }

    @PutMapping("/{id}/logo")
    public ResponseEntity<String> updateLogo(
            @PathVariable Integer id,
            @RequestParam MultipartFile logo,
            @AuthenticationPrincipal UserEntity user) {
        School school = schoolService.getByIdOrUser(id, user);
        if (!RoleBasedAccessUtils.canModifySchool(user, school)) {
            return new ResponseEntity<>("You can't modify this school", HttpStatus.FORBIDDEN);
        }
        try {
            String url = storageService.uploadFile(logo, CloudFolder.SCHOOL_IMAGES);
            school.setLogo(url);
            schoolRepository.save(school);
            return new ResponseEntity<>("Image updated", HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Unable to update logo", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSchool(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserEntity user
    ) {
        School school = schoolService.getByIdOrUser(id, user);
        if (!RoleBasedAccessUtils.canModifySchool(user, school)) {
            return new ResponseEntity<>("You can't modify this school", HttpStatus.FORBIDDEN);
        }
        try {
            schoolRepository.delete(school);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Unable to delete school", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("School deleted", HttpStatus.OK);
    }

    @GetMapping("/{id}/rating")
    public ResponseEntity<Map<String, Object>> getRating(
            @PathVariable Integer id
    ) {
        Object[] columns = schoolRepository.findSchoolAverageMarks(id);
        Map<String, Object> result = new HashMap<>();
        result.put("schoolName", columns[0]);
        result.put("averageGrade", columns[1]);
        return new ResponseEntity<>(result, HttpStatus.OK);
//        List<Object[]> results = schoolRepository.findSchoolsWithAverageMarks();
//        return new ResponseEntity<>(
//                results.stream().map(row -> {
//                    Map<String, Object> map = new HashMap<>();
//                    map.put("schoolName", row[0]);
//                    map.put("averageGrade", row[1]);
//                    return map;
//                }).collect(Collectors.toList()), HttpStatus.OK
//        );
    }

    @PostMapping("/{id}/users")
    public ResponseEntity<String> addUser(
            @PathVariable Integer id,
            @RequestBody AddSchoolUserDto addSchoolUserDto,
            @AuthenticationPrincipal UserEntity user) {
        School school = schoolService.getByIdOrUser(id, user);
        if (!RoleBasedAccessUtils.canModifySchool(user, school)) {
            return new ResponseEntity<>("You can't modify this school", HttpStatus.FORBIDDEN);
        }
        UserEntity targetUser = userService.getByUsername(addSchoolUserDto.getUsername());
        if (!RoleBasedAccessUtils.canModifyUser(user, targetUser)) {
            return new ResponseEntity<>("You can't modify this user", HttpStatus.FORBIDDEN);
        }
        targetUser.setSchool(school);
        try {
            userRepository.save(targetUser);
            return new ResponseEntity<>("User added", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Couldn't add user to specified school",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/users")
    public ResponseEntity<List<UserDto>> getUsers(
            @PathVariable Integer id,
            @RequestParam(required = false) List<String> include,
            @AuthenticationPrincipal UserEntity user
    ) {
        School school = schoolService.getByIdOrUser(id, user);
        if (!RoleBasedAccessUtils.canAccessSchool(user, school)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        List<UserDto> users = userService.mapAllToDto(schoolService.getSchoolUsersLoaded(id, include), include);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{id}/classes")
    public ResponseEntity<List<ClassDto>> getClasses(
            @PathVariable Integer id,
            @RequestParam(required = false) List<String> include,
            @AuthenticationPrincipal UserEntity user
    ) {
        School school = schoolService.getByIdOrUser(id, user);
        if (!RoleBasedAccessUtils.canAccessSchool(user, school)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        List<ClassDto> classes = classService
                .mapAllToDto(schoolService.getSchoolClassesLoaded(id, include), include);
        return new ResponseEntity<>(classes, HttpStatus.OK);
    }

    @GetMapping("/{id}/courses")
    public ResponseEntity<List<CourseDto>> getCourses(
            @PathVariable Integer id,
            @RequestParam(required = false) List<String> include,
            @AuthenticationPrincipal UserEntity user
    ) {
        School school = schoolService.getByIdOrUser(id, user);
        if (!RoleBasedAccessUtils.canAccessSchool(user, school)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        List<CourseDto> courses = courseService
                .mapAllToDto(schoolService.getSchoolCoursesLoaded(id, include), include);
        return new ResponseEntity<>(courses, HttpStatus.OK);
    }

    @GetMapping("/{id}/news")
    public ResponseEntity<List<News>> getNews(
            @PathVariable Integer id,
            @RequestParam(required = false) List<String> include,
            @AuthenticationPrincipal UserEntity user
    ) {
        School school = schoolService.getByIdOrUser(id, user);
        if (!RoleBasedAccessUtils.canAccessSchool(user, school)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(schoolService.getSchoolNewsLoaded(id, include), HttpStatus.OK);
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
