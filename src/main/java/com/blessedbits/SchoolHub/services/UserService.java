package com.blessedbits.SchoolHub.services;

import com.blessedbits.SchoolHub.models.UserEntity;
import com.blessedbits.SchoolHub.projections.dto.UserDto;
import com.blessedbits.SchoolHub.projections.mappers.UserMapper;
import com.blessedbits.SchoolHub.repositories.UserRepository;
import com.blessedbits.SchoolHub.security.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final JWTUtils jwtUtils;

    @Autowired
    public UserService(UserRepository userRepository, JWTUtils jwtUtils) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
    }

    public UserEntity getUserFromHeader(String authHeader) {
        String accessToken = jwtUtils.getJwtFromHeader(authHeader);
        String username = jwtUtils.getUsernameFromJWT(accessToken);
        return getByUsername(username);
    }

    public UserEntity getById(Integer id) {
        return userRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with given id not found")
        );
    }

    public UserEntity getByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with given username not found")
        );
    }

    public <T> T getByUsername(String username, Class<T> clazz) {
        T user = userRepository.findByUsername(username, clazz);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with given username not found");
        }
        return user;
    }

    public List<UserDto> getAllAsDto(List<String> include) {
        return userRepository.findAll().stream()
                .map(userEntity -> UserMapper.INSTANCE.toUserDto(userEntity, include))
                .toList();
    }

    public List<UserDto> fetchAllToDto(Set<UserEntity> users, List<String> include) {
        return users.stream()
                .map(userEntity -> UserMapper.INSTANCE.toUserDto(userEntity, include))
                .toList();
    }

    public List<UserDto> fetchAllToDto(List<UserEntity> users, List<String> include) {
        return users.stream()
                .map(userEntity -> UserMapper.INSTANCE.toUserDto(userEntity, include))
                .toList();
    }
}
