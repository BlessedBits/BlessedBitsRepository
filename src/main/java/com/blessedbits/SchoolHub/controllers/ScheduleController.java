package com.blessedbits.SchoolHub.controllers;

import com.blessedbits.SchoolHub.models.Schedule;
import com.blessedbits.SchoolHub.dto.ScheduleDto;
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

    @PostMapping("/new")
    public ResponseEntity<Schedule> createSchedule(@RequestBody ScheduleDto scheduleDto) {
        try 
        {
            return ResponseEntity.ok(scheduleService.createSchedule(scheduleDto));
        } catch (Exception e) 
        {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/")
    public ResponseEntity<List<Schedule>> getAllSchedules() {
        try {
            return ResponseEntity.ok(scheduleService.getAllSchedules());
        } catch (Exception e) 
        {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Schedule> getScheduleById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(scheduleService.getScheduleById(id));
        } catch (RuntimeException e) 
        {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found with ID: " + id, e);
        } catch (Exception e) 
        {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Schedule> updateSchedule(@PathVariable Integer id, @RequestBody ScheduleDto scheduleDto) {
        try {
            return ResponseEntity.ok(scheduleService.updateSchedule(id, scheduleDto));
        } catch (RuntimeException e) 
        {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found with ID: " + id, e);
        } catch (Exception e) 
        {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Integer id) {
        try {
            scheduleService.deleteSchedule(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) 
        {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found with ID: " + id, e);
        } catch (Exception e) 
        {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

