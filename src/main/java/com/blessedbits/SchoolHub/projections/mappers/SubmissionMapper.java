package com.blessedbits.SchoolHub.projections.mappers;

import com.blessedbits.SchoolHub.models.Submission;
import com.blessedbits.SchoolHub.projections.dto.SubmissionDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubmissionMapper {
    SubmissionMapper INSTANCE = Mappers.getMapper(SubmissionMapper.class);

    default SubmissionDto toSubmissionDto(Submission submission, List<String> include) {
        SubmissionDto submissionDto;
        if (include == null || include.isEmpty()) {
            submissionDto = BasicDtoMapper.toSubmissionDto(submission);
            return submissionDto;
        } else {
            submissionDto = BasicDtoMapper.toBasicSubmissionDto(submission);
        }
        if (include.contains("student")) {
            submissionDto.setStudent(BasicDtoMapper.toUserDto(submission.getStudent()));
        } else {
            submissionDto.setStudentId(submission.getStudent().getId());
        }
        if (include.contains("assignment")) {
            submissionDto.setAssignment(BasicDtoMapper.toAssignmentDto(submission.getAssignment()));
        } else {
            submissionDto.setAssignmentId(submission.getAssignment().getId());
        }
        return submissionDto;
    }
}
