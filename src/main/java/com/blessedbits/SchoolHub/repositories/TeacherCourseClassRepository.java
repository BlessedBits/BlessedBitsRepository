package com.blessedbits.SchoolHub.repositories;

import com.blessedbits.SchoolHub.models.TeacherCourseClass;
import com.blessedbits.SchoolHub.models.UserEntity;
import com.blessedbits.SchoolHub.models.Course;
import com.blessedbits.SchoolHub.models.ClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherCourseClassRepository extends JpaRepository<TeacherCourseClass, Integer> {
    boolean existsByTeacherAndCourseAndClassEntity(UserEntity teacher, Course course, ClassEntity classEntity);
    
    List<TeacherCourseClass> findByTeacher(UserEntity teacher);

    List<TeacherCourseClass> findByTeacherAndCourse(UserEntity teacher, Course course);

    List<TeacherCourseClass> findByCourseAndClassEntity(Course course, ClassEntity classEntity);

    void deleteByTeacherIdAndCourseId(Integer teacherId, Integer courseId);

    void deleteByClassEntityIdAndCourseId(Integer classEntityId, Integer courseId);

    void deleteByClassEntityIdAndCourseIdAndTeacherId(Integer classEntityId, Integer courseId, Integer teacherId);
}
