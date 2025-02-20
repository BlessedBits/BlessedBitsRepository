package com.blessedbits.SchoolHub.projections.mappers;

import com.blessedbits.SchoolHub.models.Achievement;
import com.blessedbits.SchoolHub.projections.dto.AchievementDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AchievementMapper {
    AchievementMapper INSTANCE = Mappers.getMapper(AchievementMapper.class);

    default AchievementDto toAchievementDto(Achievement achievement, List<String> include) {
        AchievementDto achievementDto;
        if (include == null || include.isEmpty()) {
            achievementDto = BasicDtoMapper.toAchievementDto(achievement);
            return achievementDto;
        } else {
            achievementDto = BasicDtoMapper.toBasicAchievementDto(achievement);
        }
        if (include.contains("school")) {
            achievementDto.setSchool(BasicDtoMapper.toSchoolDto(achievement.getSchool()));
        } else {
            achievementDto.setSchoolId(achievement.getSchool().getId());
        }
        return achievementDto;
    }
}
