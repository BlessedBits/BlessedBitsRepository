package com.blessedbits.SchoolHub.controllers;

import com.blessedbits.SchoolHub.dto.CreateSubmissionDto;
import com.blessedbits.SchoolHub.dto.GradeSubmissionDto;
import com.blessedbits.SchoolHub.misc.RoleBasedAccessUtils;
import com.blessedbits.SchoolHub.misc.RoleType;
import com.blessedbits.SchoolHub.models.Assignment;
import com.blessedbits.SchoolHub.models.Course;
import com.blessedbits.SchoolHub.models.Submission;
import com.blessedbits.SchoolHub.models.UserEntity;
import com.blessedbits.SchoolHub.projections.dto.SubmissionDto;
import com.blessedbits.SchoolHub.projections.mappers.SubmissionMapper;
import com.blessedbits.SchoolHub.repositories.SubmissionRepository;
import com.blessedbits.SchoolHub.services.AssignmentService;
import com.blessedbits.SchoolHub.services.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/submissions")
public class SubmissionController {
    private final AssignmentService assignmentService;
    private final SubmissionRepository submissionRepository;
    private final SubmissionService submissionService;
    private final RoleBasedAccessUtils roleBasedAccessUtils;

    @Autowired
    public SubmissionController(AssignmentService assignmentService, SubmissionRepository submissionRepository, SubmissionService submissionService, RoleBasedAccessUtils roleBasedAccessUtils) {
        this.assignmentService = assignmentService;
        this.submissionRepository = submissionRepository;
        this.submissionService = submissionService;
        this.roleBasedAccessUtils = roleBasedAccessUtils;
    }

    @PostMapping("")
    public ResponseEntity<String> createSubmission(
            @RequestBody CreateSubmissionDto submissionDto,
            @AuthenticationPrincipal UserEntity user
    ) {
        Assignment assignment = assignmentService.getById(submissionDto.getAssignmentId());
        if (!roleBasedAccessUtils.canAccessCourse(user, assignment.getModule().getCourse())) {
            return new ResponseEntity<>("You can't create submission for this course", HttpStatus.FORBIDDEN);
        }
        Submission submission = new Submission();
        submission.setUrl(submissionDto.getUrl());
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setStudent(user);
        submission.setAssignment(assignment);
        try {
            submissionRepository.save(submission);
            return new ResponseEntity<>("Submission created", HttpStatus.CREATED);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Unable to create submission", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubmissionDto> getSubmissionById(
            @PathVariable Long id,
            @RequestParam(required = false) List<String> include,
            @AuthenticationPrincipal UserEntity user
    ) {
        Submission submission = submissionService.getLoadedById(id, include);
        if (!roleBasedAccessUtils.canAccessCourse(user, submission.getAssignment().getModule().getCourse())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(SubmissionMapper.INSTANCE.toSubmissionDto(submission, include), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateSubmission(
            @PathVariable Long id,
            @RequestBody String url,
            @AuthenticationPrincipal UserEntity user
    ) {
        Submission submission = submissionService.getById(id);
        if (!(submission.getStudent().getId() == user.getId())) {
            return new ResponseEntity<>("You can't modify this course", HttpStatus.FORBIDDEN);
        }
        submission.setUrl(url);
        try {
            submissionRepository.save(submission);
            return new ResponseEntity<>("Submission updated", HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Unable to update submission", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSubmission(
            @PathVariable Long id,
            @AuthenticationPrincipal UserEntity user
    ) {
        Submission submission = submissionService.getById(id);
        if (!roleBasedAccessUtils.canDeleteSubmission(submission, user)) {
            return new ResponseEntity<>("You can't delete this submission", HttpStatus.FORBIDDEN);
        }
        try {
            submissionRepository.delete(submission);
            return new ResponseEntity<>("Submission deleted", HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Unable to delete submission", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
