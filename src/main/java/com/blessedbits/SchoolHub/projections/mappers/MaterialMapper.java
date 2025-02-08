package com.blessedbits.SchoolHub.projections.mappers;

import com.blessedbits.SchoolHub.models.Material;
import com.blessedbits.SchoolHub.projections.dto.MaterialDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MaterialMapper {
    MaterialMapper INSTANCE = Mappers.getMapper(MaterialMapper.class);

    default MaterialDto toMaterialDto(Material material, List<String> include) {
        MaterialDto materialDto;
        if (include == null || include.isEmpty()) {
            materialDto = BasicDtoMapper.toMaterialDto(material);
            return materialDto;
        } else {
            materialDto = BasicDtoMapper.toBasicMaterialDto(material);
        }
        if (include.contains("module")) {
            materialDto.setModule(BasicDtoMapper.toModuleDto(material.getModule()));
        } else {
            materialDto.setModuleId(material.getModule().getId());
        }
        return materialDto;
    }
}
