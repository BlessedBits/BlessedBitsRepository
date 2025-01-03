package com.blessedbits.SchoolHub.services;

import com.blessedbits.SchoolHub.models.ClassEntity;
import com.blessedbits.SchoolHub.models.Course;
import com.blessedbits.SchoolHub.models.Schedule;
import com.blessedbits.SchoolHub.repositories.ClassRepository;
import com.blessedbits.SchoolHub.repositories.CourseRepository;
import com.blessedbits.SchoolHub.repositories.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.blessedbits.SchoolHub.dto.ScheduleDto;

import java.util.List;
import java.util.Optional;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private ClassRepository classRepository; 

    @Autowired
    private CourseRepository courseRepository;

    public Schedule createSchedule(ScheduleDto scheduleDTO) {
        Optional<ClassEntity> classEntityOpt = classRepository.findById(scheduleDTO.getClassId());
        Optional<Course> courseOpt = courseRepository.findById(scheduleDTO.getCourseId());

        if (classEntityOpt.isEmpty()) 
        {
            throw new RuntimeException("Class not found with the provided ID.");
        }
        if (courseOpt.isEmpty()) 
        {
            throw new RuntimeException("Course not found with the provided ID.");
        }
        Schedule schedule = new Schedule();
        schedule.setClassEntity(classEntityOpt.get());
        schedule.setCourse(courseOpt.get()); 
        schedule.setDayOfWeek(Schedule.DayOfWeek.valueOf(scheduleDTO.getDayOfWeek()));
        schedule.setStartTime(scheduleDTO.getStartTime());
        schedule.setEndTime(scheduleDTO.getEndTime());
        schedule.setRoomNumber(scheduleDTO.getRoomNumber());

        return scheduleRepository.save(schedule);
    }

    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    public Schedule getScheduleById(Integer id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found with ID: " + id));
    }

    public Schedule updateSchedule(Integer id, ScheduleDto scheduleDTO) {
        Schedule schedule = getScheduleById(id);

        Optional<ClassEntity> classEntityOpt = classRepository.findById(scheduleDTO.getClassId());
        Optional<Course> courseOpt = courseRepository.findById(scheduleDTO.getCourseId());

        if (classEntityOpt.isEmpty() || courseOpt.isEmpty()) {
            throw new RuntimeException("Class or Course not found with the provided ID.");
        }

        schedule.setClassEntity(classEntityOpt.get()); 
        schedule.setCourse(courseOpt.get()); 
        schedule.setDayOfWeek(Schedule.DayOfWeek.valueOf(scheduleDTO.getDayOfWeek()));
        schedule.setStartTime(scheduleDTO.getStartTime());
        schedule.setEndTime(scheduleDTO.getEndTime());
        schedule.setRoomNumber(scheduleDTO.getRoomNumber());

        return scheduleRepository.save(schedule);
    }

    public void deleteSchedule(Integer id) {
        scheduleRepository.deleteById(id);
    }
}


