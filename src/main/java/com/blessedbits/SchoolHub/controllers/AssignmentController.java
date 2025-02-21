package com.blessedbits.SchoolHub.controllers;

import com.blessedbits.SchoolHub.dto.CreateAssignmentDto;
import com.blessedbits.SchoolHub.dto.GradeDto;
import com.blessedbits.SchoolHub.dto.UpdateAssignmentDto;
import com.blessedbits.SchoolHub.misc.RoleBasedAccessUtils;
import com.blessedbits.SchoolHub.models.Assignment;
import com.blessedbits.SchoolHub.models.ModuleEntity;
import com.blessedbits.SchoolHub.models.Submission;
import com.blessedbits.SchoolHub.models.UserEntity;
import com.blessedbits.SchoolHub.projections.dto.AssignmentDto;
import com.blessedbits.SchoolHub.projections.mappers.AssignmentMapper;
import com.blessedbits.SchoolHub.repositories.AssignmentRepository;
import com.blessedbits.SchoolHub.services.AssignmentService;
import com.blessedbits.SchoolHub.services.GradeService;
import com.blessedbits.SchoolHub.services.ModuleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;



@RestController
@RequestMapping("/assignments")
public class AssignmentController {
    private final ModuleService moduleService;
    private final AssignmentRepository assignmentRepository;
    private final AssignmentService assignmentService;
    private final RoleBasedAccessUtils roleBasedAccessUtils;
    private final GradeService gradeService;

    public AssignmentController(ModuleService moduleService, AssignmentRepository assignmentRepository, AssignmentService assignmentService, RoleBasedAccessUtils roleBasedAccessUtils, GradeService gradeService) {
        this.moduleService = moduleService;
        this.assignmentRepository = assignmentRepository;
        this.assignmentService = assignmentService;
        this.roleBasedAccessUtils = roleBasedAccessUtils;
        this.gradeService = gradeService;
    }

    @PostMapping("")
    public ResponseEntity<String> createAssignment(
            @RequestBody CreateAssignmentDto assignmentDto,
            @AuthenticationPrincipal UserEntity user
    ) {
        ModuleEntity moduleEntity = moduleService.getById(assignmentDto.getModuleId());
        if (!roleBasedAccessUtils.canModifyCourse(user, moduleEntity.getCourse())) {
            return new ResponseEntity<>("You can't modify this course", HttpStatus.FORBIDDEN);
        }
        Assignment assignment = new Assignment();
        assignment.setTitle(assignmentDto.getTitle());
        assignment.setDescription(assignmentDto.getDescription());
        assignment.setUrl(assignmentDto.getUrl());
        assignment.setDueDate(assignmentDto.getDueDate());
        assignment.setModule(moduleEntity);
        try {
            assignmentRepository.save(assignment);
            return new ResponseEntity<>("Assignment created", HttpStatus.CREATED);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Unable to create assignment", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssignmentDto> getAssignment(
            @PathVariable Long id,
            @RequestParam(required = false) List<String> include,
            @AuthenticationPrincipal UserEntity user
    ) {
        Assignment assignment = assignmentService.getLoadedById(id, include);
        if (!roleBasedAccessUtils.canAccessCourse(user, assignment.getModule().getCourse())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(AssignmentMapper.INSTANCE.toAssignmentDto(assignment, include), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateAssignment(
            @PathVariable Long id,
            @RequestBody UpdateAssignmentDto assignmentDto,
            @AuthenticationPrincipal UserEntity user
    ) {
        Assignment assignment = assignmentService.getById(id);
        if (!roleBasedAccessUtils.canModifyCourse(user, assignment.getModule().getCourse())) {
            return new ResponseEntity<>("You can't modify this course", HttpStatus.FORBIDDEN);
        }
        assignment.setTitle(assignmentDto.getTitle());
        assignment.setDescription(assignmentDto.getDescription());
        assignment.setUrl(assignmentDto.getUrl());
        assignment.setDueDate(assignmentDto.getDueDate());
        try {
            assignmentRepository.save(assignment);
            return new ResponseEntity<>("Assignment updated", HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Unable to update assignment", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAssignment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserEntity user
    ) {
        Assignment assignment = assignmentService.getLoadedById(id, List.of("module"));
        if (!roleBasedAccessUtils.canModifyCourse(user, assignment.getModule().getCourse())) {
            return new ResponseEntity<>("You can't modify this course", HttpStatus.FORBIDDEN);
        }
        try {
            assignmentService.deleteRelations(assignment);
            assignmentRepository.delete(assignment);
            return new ResponseEntity<>("Assignment deleted", HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Unable to delete assignment", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("{id}/submissions")
    public ResponseEntity<List<Submission>> getModuleSubmissions(
        @PathVariable Long id,
        @RequestParam(required = false) List<String> include,
        @AuthenticationPrincipal UserEntity user) 
    {
        Assignment assignment = assignmentService.getById(id);

        if (!roleBasedAccessUtils.canAccessCourse(user, assignment.getModule().getCourse())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        List<Submission> submissions = assignmentService.getAssignmentSubmissionsLoaded(id, include);
        return new ResponseEntity<>(submissions, HttpStatus.OK);
    }
  
    @PostMapping("/{id}/grade")
    public ResponseEntity<?> evaluateAssignment(@PathVariable Long id, 
                                                 @AuthenticationPrincipal UserEntity teacher, 
                                                 @RequestBody GradeDto dto) {
        try {
            return new ResponseEntity<>(gradeService.createGrade(dto, id, teacher), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    

}
