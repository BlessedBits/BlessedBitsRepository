package com.blessedbits.SchoolHub.repositories;

import com.blessedbits.SchoolHub.models.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    List<Schedule> findByClassEntity_Id(Integer classId);
    List<Schedule> findByCourse_Id(Integer courseId);    
}

