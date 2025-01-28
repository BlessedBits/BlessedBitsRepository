package com.blessedbits.SchoolHub.services;

import com.blessedbits.SchoolHub.models.ClassEntity;
import com.blessedbits.SchoolHub.repositories.ClassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ClassService {
    private final ClassRepository classRepository;

    @Autowired
    public ClassService(ClassRepository classRepository) {
        this.classRepository = classRepository;
    }

    public ClassEntity getById(Integer id) {
        return classRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Class with given id not found")
        );
    }
}
