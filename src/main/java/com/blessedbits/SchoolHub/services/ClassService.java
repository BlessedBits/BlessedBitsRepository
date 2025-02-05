package com.blessedbits.SchoolHub.services;

import com.blessedbits.SchoolHub.models.ClassEntity;
import com.blessedbits.SchoolHub.projections.dto.ClassDto;
import com.blessedbits.SchoolHub.projections.mappers.ClassMapper;
import com.blessedbits.SchoolHub.repositories.ClassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

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

    public List<ClassDto> getAllAsDto(List<String> include) {
        return classRepository.findAll().stream()
                .map(classEntity -> ClassMapper.INSTANCE.toClassDto(classEntity, include))
                .toList();
    }

    public List<ClassDto> mapAllToDto(List<ClassEntity> entities, List<String> include) {
        return entities.stream()
                .map(classEntity -> ClassMapper.INSTANCE.toClassDto(classEntity, include))
                .toList();
    }
}
