package com.blessedbits.SchoolHub.controllers;

import com.blessedbits.SchoolHub.dto.GradeDto;
import com.blessedbits.SchoolHub.models.Grade;
import com.blessedbits.SchoolHub.models.UserEntity;
import com.blessedbits.SchoolHub.services.GradeService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/grades")
public class GradeController {

    @Autowired
    private GradeService gradeService;

    @PostMapping
    public ResponseEntity<?> createGrade(@RequestBody GradeDto dto, 
                                         @AuthenticationPrincipal UserEntity teacher) {
        try {
            return new ResponseEntity<>(gradeService.createGrade(dto, null, teacher), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<Grade>> getGrades(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Integer classId,
            @RequestParam(required = false) Integer courseId,
            @RequestParam(required = false) Integer studentId,
            @RequestParam(required = false) List<String> include
    ) {
        try {
            List<Grade> grades = gradeService.getFilteredGrades(
                    studentId, classId, courseId, startDate.atStartOfDay(), endDate.atTime(23, 59, 59), include
            );
            return new ResponseEntity<>(grades, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getGradeById(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(gradeService.getGradeById(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateGrade(@PathVariable Long id, 
                                         @RequestBody GradeDto dto, 
                                         @AuthenticationPrincipal UserEntity teacher) {
        try {
            return new ResponseEntity<>(gradeService.updateGrade(id, dto, teacher), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGrade(@PathVariable Long id, @AuthenticationPrincipal UserEntity teacher) {
        try {
            gradeService.deleteGrade(id, teacher);
            return new ResponseEntity<>("Grade was successfully deleted.", HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
