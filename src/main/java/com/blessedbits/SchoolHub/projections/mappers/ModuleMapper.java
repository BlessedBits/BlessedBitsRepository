package com.blessedbits.SchoolHub.projections.mappers;

import com.blessedbits.SchoolHub.models.ModuleEntity;
import com.blessedbits.SchoolHub.projections.dto.ModuleDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ModuleMapper {
    ModuleMapper INSTANCE = Mappers.getMapper(ModuleMapper.class);

    default ModuleDto toModuleDto(ModuleEntity module, List<String> include) {
        ModuleDto moduleDto;
        if (include == null || include.isEmpty()) {
            moduleDto = BasicDtoMapper.toModuleDto(module);
            return moduleDto;
        } else {
            moduleDto = BasicDtoMapper.toBasicModuleDto(module);
        }
        if (include.contains("course")) {
            moduleDto.setCourse(BasicDtoMapper.toCourseDto(module.getCourse()));
        } else {
            moduleDto.setCourseId(module.getCourse().getId());
        }
        if (include.contains("materials")) {
            moduleDto.setMaterials(module.getMaterials().stream()
                    .map(BasicDtoMapper::toMaterialDto)
                    .toList());
        }
        if (include.contains("assignments")) {
            moduleDto.setAssignments(module.getAssignments().stream()
                    .map(BasicDtoMapper::toAssignmentDto)
                    .toList());
        }
        return moduleDto;
    }
}
