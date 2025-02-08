package com.blessedbits.SchoolHub.projections.mappers;

import com.blessedbits.SchoolHub.models.UserEntity;
import com.blessedbits.SchoolHub.projections.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    default UserDto toUserDto(UserEntity user, List<String> include) {
        UserDto userDto;
        if (include == null || include.isEmpty()) {
            userDto = BasicDtoMapper.toUserDto(user);
            return userDto;
        } else {
            userDto = BasicDtoMapper.toBasicUserDto(user);
        }
        if (include.contains("roles")) {
            userDto.setRoles(user.getRoles().stream()
                    .map(BasicDtoMapper::toRoleDto)
                    .toList());
        }
        if (include.contains("userClass")) {
            userDto.setUserClass(BasicDtoMapper.toClassDto(user.getUserClass()));
        } else {
            if (user.getUserClass() != null) {
                userDto.setUserClassId(user.getUserClass().getId());
            }
        }
        if (include.contains("school")) {
            if (user.getSchool() != null) {
                userDto.setSchool(BasicDtoMapper.toSchoolDto(user.getSchool()));
            }
        } else {
            userDto.setSchoolId(user.getUserClass().getId());
        }
        return userDto;
    }
}
