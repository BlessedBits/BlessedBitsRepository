package com.blessedbits.SchoolHub.services;

import com.blessedbits.SchoolHub.models.ClassEntity;
import com.blessedbits.SchoolHub.models.Course;
import com.blessedbits.SchoolHub.models.Schedule;
import com.blessedbits.SchoolHub.models.TeacherCourseClass;
import com.blessedbits.SchoolHub.models.UserEntity;
import com.blessedbits.SchoolHub.projections.dto.ScheduleDto;
import com.blessedbits.SchoolHub.projections.mappers.ScheduleMapper;
import com.blessedbits.SchoolHub.repositories.ClassRepository;
import com.blessedbits.SchoolHub.repositories.CourseRepository;
import com.blessedbits.SchoolHub.repositories.ScheduleRepository;
import com.blessedbits.SchoolHub.repositories.TeacherCourseClassRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.blessedbits.SchoolHub.dto.CreateScheduleDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private TeacherCourseClassRepository teacherCourseClassRepository;

    @Autowired
    private ClassRepository classRepository; 

    @Autowired
    private CourseRepository courseRepository;

    public void createSchedule(CreateScheduleDto createScheduleDTO) {
        Optional<ClassEntity> classEntityOpt = classRepository.findById(createScheduleDTO.getClassId());
        Optional<Course> courseOpt = courseRepository.findById(createScheduleDTO.getCourseId());

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
        schedule.setDayOfWeek(Schedule.DayOfWeek.valueOf(createScheduleDTO.getDayOfWeek()));
        schedule.setStartTime(createScheduleDTO.getStartTime());
        schedule.setEndTime(createScheduleDTO.getEndTime());
        schedule.setRoomNumber(createScheduleDTO.getRoomNumber());

        scheduleRepository.save(schedule);
    }

    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    public Schedule getScheduleById(Integer id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found with ID: " + id));
    }

    public Schedule updateSchedule(Integer id, CreateScheduleDto createScheduleDTO) {
        Schedule schedule = getScheduleById(id);

        Optional<ClassEntity> classEntityOpt = classRepository.findById(createScheduleDTO.getClassId());
        Optional<Course> courseOpt = courseRepository.findById(createScheduleDTO.getCourseId());

        if (classEntityOpt.isEmpty() || courseOpt.isEmpty()) {
            throw new RuntimeException("Class or Course not found with the provided ID.");
        }

        schedule.setClassEntity(classEntityOpt.get()); 
        schedule.setCourse(courseOpt.get()); 
        schedule.setDayOfWeek(Schedule.DayOfWeek.valueOf(createScheduleDTO.getDayOfWeek()));
        schedule.setStartTime(createScheduleDTO.getStartTime());
        schedule.setEndTime(createScheduleDTO.getEndTime());
        schedule.setRoomNumber(createScheduleDTO.getRoomNumber());

        return scheduleRepository.save(schedule);
    }

    public void deleteSchedule(Integer id) {
        scheduleRepository.deleteById(id);
    }

    public List<Schedule> getScheduleByClassId(Integer classId)
    {
        return scheduleRepository.findByClassEntityId(classId);
    }

    private List<Map<String, String>> getTeachersForSchedule(Schedule schedule) {
        List<UserEntity> teachers = teacherCourseClassRepository
                .findByCourseAndClassEntity(schedule.getCourse(), schedule.getClassEntity())
                .stream()
                .map(TeacherCourseClass::getTeacher)
                .toList();

        return teachers.stream()
                .map(teacher -> Map.of("firstName", teacher.getFirstName(), "lastName", teacher.getLastName()))
                .toList();
    }

    public List<ScheduleDto> mapAllToDto(List<Schedule> schedules, List<String> include) {
        return schedules.stream()
                .map(schedule -> {
                    ScheduleDto scheduleDto = ScheduleMapper.INSTANCE.toScheduleDto(schedule, include);
                    List<Map<String, String>> teachers = getTeachersForSchedule(schedule);
                    scheduleDto.setTeachers(teachers);
                    return scheduleDto;
                })
                .toList();
    }
    

}


