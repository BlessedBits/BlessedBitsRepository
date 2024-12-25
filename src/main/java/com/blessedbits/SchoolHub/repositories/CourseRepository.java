package com.blessedbits.SchoolHub.repositories;

import com.blessedbits.SchoolHub.models.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {
}
