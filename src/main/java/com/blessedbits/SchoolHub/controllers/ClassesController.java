package com.blessedbits.SchoolHub.controllers;

import com.blessedbits.SchoolHub.dto.AddClassStudentDto;
import com.blessedbits.SchoolHub.dto.AddCourseToClassDto;
import com.blessedbits.SchoolHub.dto.CreateClassDto;
import com.blessedbits.SchoolHub.misc.RoleBasedAccessUtils;
import com.blessedbits.SchoolHub.models.ClassEntity;
import com.blessedbits.SchoolHub.models.Course;
import com.blessedbits.SchoolHub.models.School;
import com.blessedbits.SchoolHub.models.UserEntity;
import com.blessedbits.SchoolHub.projections.dto.ClassDto;
import com.blessedbits.SchoolHub.projections.dto.CourseDto;
import com.blessedbits.SchoolHub.projections.dto.UserDto;
import com.blessedbits.SchoolHub.projections.mappers.ClassMapper;
import com.blessedbits.SchoolHub.projections.mappers.CourseMapper;
import com.blessedbits.SchoolHub.repositories.ClassRepository;
import com.blessedbits.SchoolHub.repositories.UserRepository;
import com.blessedbits.SchoolHub.services.ClassService;
import com.blessedbits.SchoolHub.services.CourseService;
import com.blessedbits.SchoolHub.services.SchoolService;
import com.blessedbits.SchoolHub.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/classes")
public class ClassesController {
    private final ClassRepository classRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final CourseService courseService;
    private final ClassService classService;
    private final SchoolService schoolService;

    @Autowired
    public ClassesController(ClassRepository classRepository, UserRepository userRepository,
                             UserService userService, CourseService courseService, ClassService classService, SchoolService schoolService) {
        this.classRepository = classRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.courseService = courseService;
        this.classService = classService;
        this.schoolService = schoolService;
    }

    @GetMapping("")
    public ResponseEntity<List<ClassDto>> getClasses(
            @RequestParam(required = false) List<String> include
    ) {
        return new ResponseEntity<>(classService.getAllAsDto(include), HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<String> createClass(
            @RequestBody CreateClassDto classDto,
            @AuthenticationPrincipal UserEntity user
    ) {
        School school = schoolService.getByIdOrUser(classDto.getSchoolId(), user);
        if (!RoleBasedAccessUtils.canModifySchool(user, school)) {
            return new ResponseEntity<>("You can't modify this school", HttpStatus.FORBIDDEN);
        }
        UserEntity teacher = userService.getByUsername(classDto.getHomeroomTeacher());
        if (!teacher.getSchool().equals(school)) {
            return new ResponseEntity<>("You can't modify this user", HttpStatus.FORBIDDEN);
        }
        ClassEntity classEntity = new ClassEntity();
        classEntity.setName(classDto.getName());
        classEntity.setHomeroomTeacher(teacher);
        classEntity.setSchool(school);
        try {
            classRepository.save(classEntity);
            return new ResponseEntity<>("Class created", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClassDto> getClassById(
            @PathVariable("id") Integer classId,
            @RequestParam(required = false) List<String> include,
            @AuthenticationPrincipal UserEntity user
    ) {
        ClassEntity classEntity = classService.getById(classId);
        if (!RoleBasedAccessUtils.canAccessClass(user, classEntity)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(ClassMapper.INSTANCE.toClassDto(classEntity, include), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateClass(
            @PathVariable("id") Integer classId,
            @RequestBody CreateClassDto classDto,
            @AuthenticationPrincipal UserEntity user
    ) {
        ClassEntity classEntity = classService.getById(classId);
        if (!RoleBasedAccessUtils.canModifyClass(user, classEntity)) {
            return new ResponseEntity<>("You can't modify this class", HttpStatus.FORBIDDEN);
        }
        String className = classDto.getName();
        if (className != null && !className.isEmpty()) {
            classEntity.setName(className);
        }
        if (classDto.getHomeroomTeacher() != null) {
            UserEntity teacher = userService.getByUsername(classDto.getHomeroomTeacher());
            if (!teacher.getSchool().equals(classEntity.getSchool())) {
                return new ResponseEntity<>("User doesn't belong to the school", HttpStatus.BAD_REQUEST);
            }
            classEntity.setHomeroomTeacher(teacher);
        }
        try {
            classRepository.save(classEntity);
            return new ResponseEntity<>("Class updated", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteClass(
            @PathVariable(name = "id") Integer classId,
            @AuthenticationPrincipal UserEntity user
    ) {
        ClassEntity classEntity = classService.getById(classId);
        if (!RoleBasedAccessUtils.canModifyClass(user, classEntity)) {
            return new ResponseEntity<>("You can't modify this class", HttpStatus.FORBIDDEN);
        }
        try {
            classRepository.delete(classEntity);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("Class deleted", HttpStatus.OK);
    }

    @PostMapping("/{id}/courses")
    public ResponseEntity<String> addCourse(
            @PathVariable(name = "id") Integer classId,
            @RequestBody AddCourseToClassDto addCourseToClassDto,
            @AuthenticationPrincipal UserEntity user
    ) {
        Course course = courseService.getById(addCourseToClassDto.getCourseId());
        if (!RoleBasedAccessUtils.canModifyCourse(user, course)) {
            return new ResponseEntity<>("You can't modify this course", HttpStatus.FORBIDDEN);
        }
        ClassEntity classEntity = classService.getById(classId);
        if (!RoleBasedAccessUtils.canModifyClass(user, classEntity)) {
            return new ResponseEntity<>("You can't modify this class", HttpStatus.FORBIDDEN);
        }
        if (!classEntity.getSchool().equals(course.getSchool())) {
            return new ResponseEntity<>("Class and course belong to different schools",
                    HttpStatus.BAD_REQUEST);
        }
        classEntity.addCourse(course);
        try {
            classRepository.save(classEntity);
        } catch (Exception e) {
            return new ResponseEntity<>("Unable to add course to your class",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("Course added to class successfully", HttpStatus.OK);
    }

    @GetMapping("/{id}/courses")
    public ResponseEntity<List<CourseDto>> getClassCourses(
            @PathVariable(name = "id") Integer classId,
            @RequestParam(required = false) List<String> include,
            @AuthenticationPrincipal UserEntity user
    ) {
        ClassEntity classEntity = classService.getById(classId);
        if (!RoleBasedAccessUtils.canAccessClass(user, classEntity)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        List<CourseDto> courses = courseService.mapAllToDto(classEntity.getCourses(), include);
        return new ResponseEntity<>(courses, HttpStatus.OK);
    }

    @DeleteMapping("/{id}/courses")
    public ResponseEntity<String> removeCourse(
            @PathVariable(name = "id") Integer classId,
            @RequestBody AddCourseToClassDto addCourseToClassDto,
            @AuthenticationPrincipal UserEntity user
    ) {
        ClassEntity classEntity = classService.getById(classId);
        if (!RoleBasedAccessUtils.canModifyClass(user, classEntity)) {
            return new ResponseEntity<>("You can't modify this class", HttpStatus.FORBIDDEN);
        }
        Course course = courseService.getById(addCourseToClassDto.getCourseId());
        if (!RoleBasedAccessUtils.canModifyCourse(user, course)) {
            return new ResponseEntity<>("You can't modify this course", HttpStatus.FORBIDDEN);
        }
        if (!classEntity.getCourses().contains(course)) {
            return new ResponseEntity<>("This course does not belong to this class", HttpStatus.BAD_REQUEST);
        }
        try {
            classEntity.removeCourse(course);
            classRepository.save(classEntity);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("Course removed from class", HttpStatus.OK);
    }

    @PostMapping("/{id}/students")
    public ResponseEntity<String> addStudent(
            @PathVariable(name = "id") Integer classId,
            @RequestBody AddClassStudentDto addClassStudentDto,
            @AuthenticationPrincipal UserEntity user
    ) {
        ClassEntity classEntity = classService.getById(classId);
        if (!RoleBasedAccessUtils.canModifyClass(user, classEntity)) {
            return new ResponseEntity<>("You can't modify this class", HttpStatus.FORBIDDEN);
        }
        UserEntity student = userService.getByUsername(addClassStudentDto.getUsername());
        if (student.getSchool() != classEntity.getSchool()) {
            return new ResponseEntity<>("User and class belong to different schools",
                    HttpStatus.BAD_REQUEST);
        }
        classEntity.addStudent(student);
        try {
            classRepository.save(classEntity);
            userRepository.save(student);
        } catch (Exception e) {
            return new ResponseEntity<>("Couldn't add user to specified class",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("Student added to specified class", HttpStatus.OK);
    }

    @GetMapping("/{id}/students")
    public ResponseEntity<List<UserDto>> getClassStudents(
            @PathVariable(name = "id") Integer classId,
            @RequestParam(required = false) List<String> include,
            @AuthenticationPrincipal UserEntity user
    ) {
        ClassEntity classEntity = classService.getById(classId);
        if (!RoleBasedAccessUtils.canAccessClass(user, classEntity)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        List<UserDto> students = userService.fetchAllToDto(classEntity.getStudents(), include);
        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    @DeleteMapping("/{id}/students")
    public ResponseEntity<String> removeStudent(
            @PathVariable(name = "id") Integer classId,
            @RequestBody AddClassStudentDto addClassStudentDto,
            @AuthenticationPrincipal UserEntity user
    ) {
        ClassEntity classEntity = classService.getById(classId);
        if (!RoleBasedAccessUtils.canModifyClass(user, classEntity)) {
            return new ResponseEntity<>("You can't modify this class", HttpStatus.FORBIDDEN);
        }
        UserEntity student = userService.getByUsername(addClassStudentDto.getUsername());
        if (!classEntity.getStudents().contains(student)) {
            return new ResponseEntity<>("This student does not belong to this class", HttpStatus.BAD_REQUEST);
        }
        try {
            classEntity.removeStudent(student);
            classRepository.save(classEntity);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("Student removed from specified class", HttpStatus.OK);
    }

}
