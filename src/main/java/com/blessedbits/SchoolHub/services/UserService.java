package com.blessedbits.SchoolHub.services;

import com.blessedbits.SchoolHub.models.UserEntity;
import com.blessedbits.SchoolHub.repositories.UserRepository;
import com.blessedbits.SchoolHub.security.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class UserService {
    private UserRepository userRepository;
    private JWTUtils jwtUtils;

    @Autowired
    public UserService(UserRepository userRepository, JWTUtils jwtUtils) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
    }

    public UserEntity getUserFromHeader(String authHeader) {
        String accessToken = jwtUtils.getJwtFromHeader(authHeader);
        String username = jwtUtils.getUsernameFromJWT(accessToken);
        Optional<UserEntity> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return userOptional.get();
    }
}
