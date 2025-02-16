package com.blessedbits.SchoolHub.projections.mappers;

import com.blessedbits.SchoolHub.models.*;
import com.blessedbits.SchoolHub.projections.dto.*;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BasicDtoMapper {
    static UserDto toBasicUserDto(UserEntity user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        userDto.setConfirmed(user.getIsConfirmed());
        userDto.setProfileImage(user.getProfileImage());
        userDto.setDuty(user.getDuty());
        userDto.setRole(String.valueOf(user.getRole()));
        return userDto;
    }

    static UserDto toUserDto(UserEntity user) {
        UserDto userDto = toBasicUserDto(user);
        if (user.getUserClass() != null) {
            userDto.setUserClassId(user.getUserClass().getId());
        }
        if (user.getSchool() != null) {
            userDto.setSchoolId(user.getSchool().getId());
        }
        return userDto;
    }

    static SchoolDto toBasicSchoolDto(School school) {
        SchoolDto schoolDto = new SchoolDto();
        schoolDto.setId(school.getId());
        schoolDto.setName(school.getName());
        schoolDto.setAddress(school.getAddress());
        schoolDto.setLogo(school.getLogo());
        return schoolDto;
    }

    static SchoolDto toSchoolDto(School school) {
        SchoolDto schoolDto = toBasicSchoolDto(school);
        return schoolDto;
    }

    static CourseDto toBasicCourseDto(Course course) {
        CourseDto courseDto = new CourseDto();
        courseDto.setId(course.getId());
        courseDto.setName(course.getName());
        return courseDto;
    }

    static CourseDto toCourseDto(Course course) {
        CourseDto courseDto = toBasicCourseDto(course);
        courseDto.setSchoolId(course.getSchool().getId());
        return courseDto;
    }

    static ClassDto toBasicClassDto(ClassEntity classEntity) {
        ClassDto classDto = new ClassDto();
        classDto.setId(classEntity.getId());
        classDto.setName(classEntity.getName());
        return classDto;
    }

    static ClassDto toClassDto(ClassEntity classEntity) {
        ClassDto classDto = toBasicClassDto(classEntity);
        classDto.setHomeroomTeacherId(classEntity.getHomeroomTeacher().getId());
        classDto.setSchoolId(classEntity.getSchool().getId());
        return classDto;
    }

    static ModuleDto toBasicModuleDto(ModuleEntity moduleEntity) {
        ModuleDto moduleDto = new ModuleDto();
        moduleDto.setId(moduleEntity.getId());
        moduleDto.setName(moduleEntity.getName());
        moduleDto.setVisible(moduleEntity.getIsVisible());
        return moduleDto;
    }

    static ModuleDto toModuleDto(ModuleEntity moduleEntity) {
        ModuleDto moduleDto = toBasicModuleDto(moduleEntity);
        moduleDto.setCourseId(moduleEntity.getCourse().getId());
        return moduleDto;
    }

    static MaterialDto toBasicMaterialDto(Material material) {
        MaterialDto materialDto = new MaterialDto();
        materialDto.setId(material.getId());
        materialDto.setTitle(material.getTitle());
        materialDto.setDescription(material.getDescription());
        materialDto.setUrl(material.getUrl());
        return materialDto;
    }

    static MaterialDto toMaterialDto(Material material) {
        MaterialDto materialDto = toBasicMaterialDto(material);
        materialDto.setModuleId(material.getModule().getId());
        return materialDto;
    }

    static AssignmentDto toBasicAssignmentDto(Assignment assignment) {
        AssignmentDto assignmentDto = new AssignmentDto();
        assignmentDto.setId(assignment.getId());
        assignmentDto.setTitle(assignment.getTitle());
        assignmentDto.setDescription(assignment.getDescription());
        assignmentDto.setUrl(assignment.getUrl());
        assignmentDto.setDueDate(assignment.getDueDate());
        return assignmentDto;
    }

    static AssignmentDto toAssignmentDto(Assignment assignment) {
        AssignmentDto assignmentDto = toBasicAssignmentDto(assignment);
        assignmentDto.setModuleId(assignment.getModule().getId());
        return assignmentDto;
    }

    static SubmissionDto toBasicSubmissionDto(Submission submission) {
        SubmissionDto submissionDto = new SubmissionDto();
        submissionDto.setId(submission.getId());
        submissionDto.setUrl(submission.getUrl());
        submissionDto.setSubmittedAt(submission.getSubmittedAt());
        submissionDto.setGrade(submission.getGrade());
        submissionDto.setGradedAt(submission.getGradedAt());
        return submissionDto;
    }

    static SubmissionDto toSubmissionDto(Submission submission) {
        SubmissionDto submissionDto = toBasicSubmissionDto(submission);
        submissionDto.setStudentId(submission.getStudent().getId());
        submissionDto.setTeacherId(submission.getTeacher().getId());
        submissionDto.setAssignmentId(submission.getAssignment().getId());
        return submissionDto;
    }

    static ScheduleDto toBasicScheduleDto(Schedule schedule) {
        ScheduleDto scheduleDto = new ScheduleDto();
        scheduleDto.setId(schedule.getId());
        scheduleDto.setDayOfWeek(schedule.getDayOfWeek());
        scheduleDto.setStartTime(schedule.getStartTime());
        scheduleDto.setEndTime(schedule.getEndTime());
        scheduleDto.setRoom(schedule.getRoomNumber());
        return scheduleDto;
    }

    static ScheduleDto toScheduleDto(Schedule schedule) {
        ScheduleDto scheduleDto = toBasicScheduleDto(schedule);
        scheduleDto.setCourseId(schedule.getCourse().getId());
        scheduleDto.setSchoolClassId(schedule.getClassEntity().getId());
        return scheduleDto;
    }
}
