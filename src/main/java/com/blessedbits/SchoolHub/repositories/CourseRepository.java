package com.blessedbits.SchoolHub.repositories;

import com.blessedbits.SchoolHub.models.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> 
{    
    Optional<Course> findById(Integer id);
    Optional<Course> findCourseByNameAndSchoolId(String name, Integer schoolId);
}
