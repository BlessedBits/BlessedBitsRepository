package com.blessedbits.SchoolHub.services;

import com.blessedbits.SchoolHub.misc.EntityManagerUtils;
import com.blessedbits.SchoolHub.models.Material;
import com.blessedbits.SchoolHub.models.ModuleEntity;
import com.blessedbits.SchoolHub.repositories.MaterialRepository;
import com.blessedbits.SchoolHub.repositories.ModuleRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class MaterialService {
    private final MaterialRepository materialRepository;
    private final ModuleRepository moduleRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public MaterialService(MaterialRepository materialRepository, ModuleRepository moduleRepository) {
        this.materialRepository = materialRepository;
        this.moduleRepository = moduleRepository;
    }

    public Material getById(Long id) {
        return materialRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Couldn't find material with specified id"));
    }

    @Transactional
    public void deleteRelations(Material material) {
        ModuleEntity module = material.getModule();
        module.getMaterials().remove(material);
        moduleRepository.save(module);
    }

    public Material getLoadedById(Long id, List<String> include) {
        String jpql = "SELECT m FROM Material m WHERE m.id = :id";
        TypedQuery<Material> query = EntityManagerUtils
                .createTypedQueryWithGraph(Material.class, entityManager, jpql, include);
        query.setParameter("id", id);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Couldn't find material");
        } catch (Exception e) {
            System.out.println("Unable to execute query with entity graph");
            return getById(id);
        }
    }
}
