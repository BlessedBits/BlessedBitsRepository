package com.blessedbits.SchoolHub.controllers;

import com.blessedbits.SchoolHub.dto.AchievementDto;
import com.blessedbits.SchoolHub.dto.AddSchoolUserDto;
import com.blessedbits.SchoolHub.dto.CreateSchoolDto;
import com.blessedbits.SchoolHub.dto.SchoolContactsDto;
import com.blessedbits.SchoolHub.misc.CloudFolder;
import com.blessedbits.SchoolHub.misc.RoleBasedAccessUtils;
import com.blessedbits.SchoolHub.misc.RoleType;
import com.blessedbits.SchoolHub.models.News;
import com.blessedbits.SchoolHub.models.Achievement;
import com.blessedbits.SchoolHub.models.School;
import com.blessedbits.SchoolHub.models.SchoolGallery;
import com.blessedbits.SchoolHub.models.UserEntity;
import com.blessedbits.SchoolHub.projections.dto.ClassDto;
import com.blessedbits.SchoolHub.projections.dto.CourseDto;
import com.blessedbits.SchoolHub.projections.dto.SchoolDto;
import com.blessedbits.SchoolHub.projections.dto.UserDto;
import com.blessedbits.SchoolHub.projections.mappers.BasicDtoMapper;
import com.blessedbits.SchoolHub.projections.mappers.SchoolMapper;
import com.blessedbits.SchoolHub.projections.mappers.UserMapper;
import com.blessedbits.SchoolHub.repositories.SchoolRepository;
import com.blessedbits.SchoolHub.repositories.UserRepository;
import com.blessedbits.SchoolHub.services.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
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
    private final RoleBasedAccessUtils roleBasedAccessUtils;

    public SchoolController(SchoolService schoolService, SchoolRepository schoolRepository, StorageService storageService, UserService userService, UserRepository userRepository, ClassService classService, CourseService courseService, RoleBasedAccessUtils roleBasedAccessUtils) {
        this.schoolRepository = schoolRepository;
        this.storageService = storageService;
        this.userService = userService;
        this.userRepository = userRepository;
        this.schoolService = schoolService;
        this.classService = classService;
        this.courseService = courseService;
        this.roleBasedAccessUtils = roleBasedAccessUtils;
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
            return new ResponseEntity<>("School created", HttpStatus.CREATED);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Unable to create school", HttpStatus.BAD_REQUEST);
        }
    }
      
    @GetMapping("/school")
    public ResponseEntity<?> getSchoolV(@RequestHeader("Authorization") String authorizationHeader) {
        Integer schoolId = userService.getUserFromHeader(authorizationHeader).getSchool().getId();
        try{
            return new ResponseEntity<>(schoolService.getSchoolInfo(schoolId), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); 
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
        School school = schoolService.getLoadedById(id, include);
        SchoolDto schoolDto;
        if (roleBasedAccessUtils.canAccessSchool(user, school)) {
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
        if (!roleBasedAccessUtils.canModifySchool(user, school)) {
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
            return new ResponseEntity<>("School updated", HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Unable to update info", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}/logo")
    public ResponseEntity<String> updateLogo(
            @PathVariable Integer id,
            @RequestParam MultipartFile logo,
            @AuthenticationPrincipal UserEntity user) {
        School school = schoolService.getByIdOrUser(id, user);
        if (!roleBasedAccessUtils.canModifySchool(user, school)) {
            return new ResponseEntity<>("You can't modify this school", HttpStatus.FORBIDDEN);
        }
        try {
            if (school.getLogo() != null && !school.getLogo().isEmpty()) {
                storageService.deleteFile(school.getLogo());
            }
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
        if (!roleBasedAccessUtils.canModifySchool(user, school)) {
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
    public ResponseEntity<?> getRating(
            @PathVariable Integer id
    ) {
        try
        {
            Object[] columns = schoolRepository.findSchoolAverageMarks(id).getFirst();
            Map<String, Object> result = new HashMap<>();
            result.put("schoolName", columns[0]);
            result.put("averageGrade", columns[1]);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e)
        {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{id}/users")
    public ResponseEntity<String> addUser(
            @PathVariable Integer id,
            @RequestBody AddSchoolUserDto addSchoolUserDto,
            @AuthenticationPrincipal UserEntity user) {
        School school = schoolService.getByIdOrUser(id, user);
        if (!roleBasedAccessUtils.canModifySchool(user, school)) {
            return new ResponseEntity<>("You can't modify this school", HttpStatus.FORBIDDEN);
        }
        UserEntity targetUser = userService.getByUsername(addSchoolUserDto.getUsername());
        if (!roleBasedAccessUtils.canModifyUser(user, targetUser)) {
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
        if (!roleBasedAccessUtils.canAccessSchool(user, school)) {
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
        if (!roleBasedAccessUtils.canAccessSchool(user, school)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        if (user.hasRole(RoleType.STUDENT)) {
            include = new ArrayList<>();
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
        if (!roleBasedAccessUtils.canAccessSchool(user, school)) {
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
        if (!roleBasedAccessUtils.canAccessSchool(user, school)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(schoolService.getSchoolNewsLoaded(id, include), HttpStatus.OK);
    }

    @PostMapping("/add-gallery-image")
    public ResponseEntity<String> addGalleryImage(@RequestHeader("Authorization") String authorizationHeader, @RequestParam("image") MultipartFile file) {
        try {
            String url = schoolService.addImageToSchoolGallery(authorizationHeader, file);
            return new ResponseEntity<>("Gallery image added successfully on link: " + url, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/all-gallery-images")
    public ResponseEntity<List<SchoolGallery>> getAllGalleryImages(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            List<SchoolGallery> images = schoolService.getAllGalleryImages(authorizationHeader);
            return new ResponseEntity<>(images, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete-gallery-image")
    public ResponseEntity<String> deleteGalleryImage(@RequestHeader("Authorization") String authorizationHeader, @RequestParam("image") String image) {
        try {
            schoolService.deleteGalleryImage(authorizationHeader, image);
            return new ResponseEntity<>("Gallery image deleted successfully", HttpStatus.NO_CONTENT);
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
    
    @GetMapping("/{id}/teachers")
    public ResponseEntity<List<UserDto>> getTeachersList(
            @PathVariable Integer id,
            @RequestParam(required = false) List<String> include,
            @AuthenticationPrincipal UserEntity user
    ) {
        School school = schoolService.getByIdOrUser(id, user);
        List<UserEntity> teachers = schoolService.getTeachersBySchool(school.getId());
        List<UserDto> teacherDtos = teachers.stream()
            .map(teacher -> {
                UserDto teacherDto = UserMapper.INSTANCE.toUserDto(teacher, include);
                userService.getTeacherCourseClasses(teacher, teacherDto, include);
                return teacherDto;
            })
            .collect(Collectors.toList());
        return new ResponseEntity<>(teacherDtos, HttpStatus.OK);
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
