package com.blessedbits.SchoolHub.controllers;

import com.blessedbits.SchoolHub.models.Schedule;
import com.blessedbits.SchoolHub.dto.CreateScheduleDto;
import com.blessedbits.SchoolHub.services.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/schedules")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @PostMapping("")
    public ResponseEntity<String> createSchedule(@RequestBody CreateScheduleDto createScheduleDto) {
        try 
        {
            scheduleService.createSchedule(createScheduleDto);
            return new ResponseEntity<String>("Schedule was successfully created!", HttpStatus.CREATED);
        } catch (Exception e) 
        {
            return new ResponseEntity<String>("Error " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("")
    public ResponseEntity<List<Schedule>> getAllSchedules() {
        try {
            return new ResponseEntity<>(scheduleService.getAllSchedules(), HttpStatus.OK);
        } catch (Exception e) 
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error: Couldn't return all schedules", e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Schedule> getScheduleById(@PathVariable Integer id) {
        try {
            return new ResponseEntity<>(scheduleService.getScheduleById(id), HttpStatus.OK);
        } catch (RuntimeException e) 
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found with ID: " + id, e);
        } catch (Exception e) 
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateSchedule(@PathVariable Integer id, @RequestBody CreateScheduleDto createScheduleDto) {
        try {
            scheduleService.updateSchedule(id, createScheduleDto);
            return new ResponseEntity<>("Schedule was successfully updated!", HttpStatus.OK);
        } catch (RuntimeException e) 
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found with ID: " + id, e);
        } catch (Exception e) 
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Integer id) {
        try {
            scheduleService.deleteSchedule(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) 
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found with ID: " + id, e);
        } catch (Exception e) 
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/class/{id}")
    public ResponseEntity<?> getScheduleByClassId(@PathVariable Integer id, @RequestParam(required = false) List<String> include) {
        try {
            List<Schedule> schedules = scheduleService.getScheduleByClassId(id);
            if (schedules.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No schedules found for class ID: " + id);
            }
            return new ResponseEntity<>(scheduleService.mapAllToDto(schedules, include), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

}

