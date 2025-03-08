package com.blessedbits.SchoolHub.controllers;

import com.blessedbits.SchoolHub.dto.*;
import com.blessedbits.SchoolHub.misc.RoleBasedAccessUtils;
import com.blessedbits.SchoolHub.models.*;
import com.blessedbits.SchoolHub.projections.dto.CourseDto;
import com.blessedbits.SchoolHub.projections.dto.ModuleDto;
import com.blessedbits.SchoolHub.projections.mappers.CourseMapper;
import com.blessedbits.SchoolHub.repositories.*;
import com.blessedbits.SchoolHub.services.CourseService;
import com.blessedbits.SchoolHub.services.ModuleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseController {
    private final CourseRepository courseRepository;
    private final CourseService courseService;
    private final ModuleService moduleService;
    private final RoleBasedAccessUtils roleBasedAccessUtils;

    public CourseController(
            CourseRepository courseRepository, CourseService courseService,
            ModuleService moduleService, RoleBasedAccessUtils roleBasedAccessUtils
    ) {
        this.courseRepository = courseRepository;
        this.courseService = courseService;
        this.moduleService = moduleService;
        this.roleBasedAccessUtils = roleBasedAccessUtils;
    }

    @GetMapping("")
    public ResponseEntity<List<CourseDto>> getCourses(
            @RequestParam(required = false) List<String> include
    ) {
        return new ResponseEntity<>(courseService.getAllAsDto(include), HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<String> createCourse(
            @RequestBody CreateCourseDto courseDto,
            @AuthenticationPrincipal UserEntity user
    ) {
        return courseService.createCourse(courseDto, user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseDto> getCourse(
            @PathVariable Integer id,
            @RequestParam(required = false) List<String> include,
            @AuthenticationPrincipal UserEntity user
    ) {
        Course course = courseService.getLoadedById(id, include);
        if (!roleBasedAccessUtils.canAccessCourse(user, course)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        CourseDto courseDto = CourseMapper.INSTANCE
                .toCourseDto(course, include);
        return new ResponseEntity<>(courseDto, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateCourse(
            @PathVariable Integer id,
            @RequestBody CourseDto courseDto,
            @AuthenticationPrincipal UserEntity user
    ) {
        Course course = courseService.getById(id);
        if (!roleBasedAccessUtils.canModifyCourse(user, course)) {
            return new ResponseEntity<>("You can't modify this course", HttpStatus.FORBIDDEN);
        }
        course.setName(courseDto.getName());
        try {
            courseRepository.save(course);
            return new ResponseEntity<>("Course updated", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Unable to update course", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCourse(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserEntity user
    ) {
        Course course = courseService.getLoadedById(id, List.of("school"));
        if (!roleBasedAccessUtils.canModifyCourse(user, course)) {
            return new ResponseEntity<>("You can't modify this course", HttpStatus.FORBIDDEN);
        }
        try {
            courseService.deleteRelations(course);
            courseRepository.delete(course);
            return new ResponseEntity<>("Course deleted", HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Unable to delete course", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/modules")
    public ResponseEntity<List<ModuleDto>> getModules(
            @PathVariable Integer id,
            @RequestParam(required = false) List<String> include,
            @AuthenticationPrincipal UserEntity user
    ) {
        Course course = courseService.getById(id);
        if (!roleBasedAccessUtils.canAccessCourse(user, course)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        List<ModuleDto> modules = moduleService
                .mapAllToDto(courseService.getCourseModulesLoaded(id, include), include);
        return new ResponseEntity<>(modules, HttpStatus.OK);
    }

    @PostMapping("/{id}/teachers")
    public ResponseEntity<String> addTeacher(@PathVariable Integer id, @RequestParam Integer teacherId,
    @AuthenticationPrincipal UserEntity user) {
        try{
            courseService.addTeacherToCourse(user, id, teacherId);
            return new ResponseEntity<>("Teacher added successfully", HttpStatus.OK);               
        }catch(Exception e)
        {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}/teachers")
    public ResponseEntity<String> removeTeacher(@PathVariable Integer id, @RequestParam Integer teacherId,
    @AuthenticationPrincipal UserEntity user) {
        try{
        courseService.removeTeacherFromCourse(user, id, teacherId);
        return new ResponseEntity<>("Teacher removed successfully", HttpStatus.OK);
        }catch(Exception e)
        {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
