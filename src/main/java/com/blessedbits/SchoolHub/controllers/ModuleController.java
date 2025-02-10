package com.blessedbits.SchoolHub.controllers;

import com.blessedbits.SchoolHub.dto.CreateModuleDto;
import com.blessedbits.SchoolHub.dto.UpdateModuleDto;
import com.blessedbits.SchoolHub.misc.RoleBasedAccessUtils;
import com.blessedbits.SchoolHub.models.Assignment;
import com.blessedbits.SchoolHub.models.Course;
import com.blessedbits.SchoolHub.models.Material;
import com.blessedbits.SchoolHub.models.ModuleEntity;
import com.blessedbits.SchoolHub.models.UserEntity;
import com.blessedbits.SchoolHub.projections.dto.ModuleDto;
import com.blessedbits.SchoolHub.projections.mappers.ModuleMapper;
import com.blessedbits.SchoolHub.repositories.ModuleRepository;
import com.blessedbits.SchoolHub.services.CourseService;
import com.blessedbits.SchoolHub.services.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/modules")
public class ModuleController {
    private final ModuleRepository moduleRepository;
    private final ModuleService moduleService;
    private final CourseService courseService;

    @Autowired
    public ModuleController(final ModuleRepository moduleRepository, final ModuleService moduleService, CourseService courseService) {
        this.moduleRepository = moduleRepository;
        this.moduleService = moduleService;
        this.courseService = courseService;
    }

    @PostMapping("")
    public ResponseEntity<String> createModule(
            @RequestBody CreateModuleDto createModuleDto,
            @AuthenticationPrincipal UserEntity user
    ) {
        Course course = courseService.getById(createModuleDto.getCourseId());
        if (!RoleBasedAccessUtils.canModifyCourse(user, course)) {
            return new ResponseEntity<>("You can't modify this course", HttpStatus.FORBIDDEN);
        }
        ModuleEntity moduleEntity = new ModuleEntity();
        moduleEntity.setName(createModuleDto.getName());
        moduleEntity.setCourse(course);
        try {
            moduleRepository.save(moduleEntity);
            return new ResponseEntity<>("Module created", HttpStatus.CREATED);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Unable to create module", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ModuleDto> getModule(
            @PathVariable long id,
            @RequestParam(required = false) List<String> include,
            @AuthenticationPrincipal UserEntity user
    ) {
        ModuleEntity moduleEntity = moduleService.getLoadedById(id, include);
        if (!RoleBasedAccessUtils.canAccessCourse(user, moduleEntity.getCourse())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(ModuleMapper.INSTANCE.toModuleDto(moduleEntity, include), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateModule(
            @PathVariable Long id,
            @RequestBody UpdateModuleDto updateModuleDto,
            @AuthenticationPrincipal UserEntity user
    ) {
        ModuleEntity moduleEntity = moduleService.getById(id);
        if (!RoleBasedAccessUtils.canModifyCourse(user, moduleEntity.getCourse())) {
            return new ResponseEntity<>("You can't modify this course", HttpStatus.FORBIDDEN);
        }
        try {
            moduleEntity.setName(updateModuleDto.getName());
            moduleRepository.save(moduleEntity);
            return new ResponseEntity<>("Module updated", HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Unable to update module", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteModule(
            @PathVariable Long id,
            @AuthenticationPrincipal UserEntity user
    ) {
        ModuleEntity moduleEntity = moduleService.getById(id);
        if (!RoleBasedAccessUtils.canModifyCourse(user, moduleEntity.getCourse())) {
            return new ResponseEntity<>("You can't modify this course", HttpStatus.FORBIDDEN);
        }
        try {
            moduleRepository.delete(moduleEntity);
            return new ResponseEntity<>("Module deleted", HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Unable to delete module", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/materials")
    public ResponseEntity<List<Material>> getModuleMaterials(
            @PathVariable Long id,
            @RequestParam(required = false) List<String> include,
            @AuthenticationPrincipal UserEntity user
    ) {
        ModuleEntity module = moduleService.getById(id);

        if (!RoleBasedAccessUtils.canAccessCourse(user, module.getCourse())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        List<Material> materials = moduleService.getModuleMaterialsLoaded(id, include);
        return new ResponseEntity<>(materials, HttpStatus.OK);
    }

    @GetMapping("{id}/assignments")
    public ResponseEntity<List<Assignment>> getModuleAssignments(
        @PathVariable Long id,
        @RequestParam(required = false) List<String> include,
        @AuthenticationPrincipal UserEntity user) 
    {
        ModuleEntity module = moduleService.getById(id);

        if (!RoleBasedAccessUtils.canAccessCourse(user, module.getCourse())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        List<Assignment> assignments = moduleService.getModuleAssignmentsLoaded(id, include);
        return new ResponseEntity<>(assignments, HttpStatus.OK);
    }
    
}

