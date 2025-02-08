package com.blessedbits.SchoolHub.services;

import com.blessedbits.SchoolHub.misc.EntityManagerUtils;
import com.blessedbits.SchoolHub.models.ModuleEntity;
import com.blessedbits.SchoolHub.projections.dto.ModuleDto;
import com.blessedbits.SchoolHub.projections.mappers.ModuleMapper;
import com.blessedbits.SchoolHub.repositories.ModuleRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ModuleService {
    private final ModuleRepository moduleRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public ModuleService(ModuleRepository moduleRepository) {
        this.moduleRepository = moduleRepository;
    }

    public ModuleEntity getById(Long id) {
        return moduleRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Module with given id not found"));
    }

    public ModuleEntity getLoadedById(Long id, List<String> include) {
        String jpql = "SELECT m FROM ModuleEntity m WHERE m.id = :id";
        TypedQuery<ModuleEntity> query = EntityManagerUtils
                .createTypedQueryWithGraph(ModuleEntity.class, entityManager, jpql, include);
        query.setParameter("id", id);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Module with given id not found");
        } catch (Exception e) {
            System.out.println("Unable to execute query with entity graph");
            return getById(id);
        }
    }

    public List<ModuleDto> mapAllToDto(List<ModuleEntity> moduleEntities, List<String> include) {
        return moduleEntities.stream()
                .map(moduleEntity -> ModuleMapper.INSTANCE.toModuleDto(moduleEntity, include))
                .toList();
    }
}
