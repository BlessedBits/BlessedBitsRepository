package com.blessedbits.SchoolHub.projections.mappers;

import com.blessedbits.SchoolHub.models.Assignment;
import com.blessedbits.SchoolHub.projections.dto.AssignmentDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AssignmentMapper {
    AssignmentMapper INSTANCE = Mappers.getMapper(AssignmentMapper.class);

    default AssignmentDto toAssignmentDto(Assignment assignment, List<String> include) {
        AssignmentDto assignmentDto;
        if (include == null || include.isEmpty()) {
            assignmentDto = BasicDtoMapper.toAssignmentDto(assignment);
            return assignmentDto;
        } else {
            assignmentDto = BasicDtoMapper.toBasicAssignmentDto(assignment);
        }
        if (include.contains("module")) {
            assignmentDto.setModule(BasicDtoMapper.toModuleDto(assignment.getModule()));
        } else {
            assignmentDto.setModuleId(assignment.getModule().getId());
        }
        if (include.contains("submissions")) {
            assignmentDto.setSubmissions(assignment.getSubmissions().stream()
                    .map(BasicDtoMapper::toSubmissionDto)
                    .toList());
        }
        return assignmentDto;
    }
}
