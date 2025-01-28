package com.blessedbits.SchoolHub.services;

import com.blessedbits.SchoolHub.models.School;
import com.blessedbits.SchoolHub.models.UserEntity;
import com.blessedbits.SchoolHub.repositories.SchoolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SchoolService {
    private final SchoolRepository schoolRepository;

    @Autowired
    public SchoolService(SchoolRepository schoolRepository) {
        this.schoolRepository = schoolRepository;
    }

    public School getById(Integer id) {
        return schoolRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "School with given id not found")
        );
    }

    public School getByIdOrUser(Integer id, UserEntity user) {
        if (id == null) {
            return user.getSchool();
        }
        return getById(id);
    }
}
