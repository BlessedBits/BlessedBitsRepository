package com.blessedbits.SchoolHub.controllers;

import com.blessedbits.SchoolHub.dto.CreateMaterialDto;
import com.blessedbits.SchoolHub.dto.UpdateMaterialDto;
import com.blessedbits.SchoolHub.misc.RoleBasedAccessUtils;
import com.blessedbits.SchoolHub.models.Material;
import com.blessedbits.SchoolHub.models.ModuleEntity;
import com.blessedbits.SchoolHub.models.UserEntity;
import com.blessedbits.SchoolHub.projections.dto.MaterialDto;
import com.blessedbits.SchoolHub.projections.mappers.MaterialMapper;
import com.blessedbits.SchoolHub.repositories.MaterialRepository;
import com.blessedbits.SchoolHub.services.MaterialService;
import com.blessedbits.SchoolHub.services.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/materials")
public class MaterialController {
    private final ModuleService moduleService;
    private final MaterialRepository materialRepository;
    private final MaterialService materialService;

    @Autowired
    public MaterialController(ModuleService moduleService, MaterialRepository materialRepository, MaterialService materialService) {
        this.moduleService = moduleService;
        this.materialRepository = materialRepository;
        this.materialService = materialService;
    }

    @PostMapping("")
    public ResponseEntity<String> createMaterial(
            @RequestBody CreateMaterialDto createMaterialDto,
            @AuthenticationPrincipal UserEntity user
    ) {
        ModuleEntity moduleEntity = moduleService.getById(createMaterialDto.getModuleId());
        if (!RoleBasedAccessUtils.canModifyCourse(user, moduleEntity.getCourse())) {
            return new ResponseEntity<>("You can't modify this course", HttpStatus.FORBIDDEN);
        }
        Material material = new Material();
        material.setTitle(createMaterialDto.getTitle());
        material.setDescription(createMaterialDto.getDescription());
        material.setUrl(createMaterialDto.getUrl());
        material.setModule(moduleEntity);
        try {
            materialRepository.save(material);
            return new ResponseEntity<>("Material created", HttpStatus.CREATED);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Unable to create material", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaterialDto> getMaterial(
            @PathVariable Long id,
            @RequestParam(required = false) List<String> include,
            @AuthenticationPrincipal UserEntity user
    ) {
        Material materialEntity = materialService.getLoadedById(id, include);
        if (!RoleBasedAccessUtils.canAccessCourse(user, materialEntity.getModule().getCourse())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(MaterialMapper.INSTANCE.toMaterialDto(materialEntity, include), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateMaterial(
            @PathVariable Long id,
            @RequestBody UpdateMaterialDto updateMaterialDto,
            @AuthenticationPrincipal UserEntity user
    ) {
        Material materialEntity = materialService.getById(id);
        if (!RoleBasedAccessUtils.canModifyCourse(user, materialEntity.getModule().getCourse())) {
            return new ResponseEntity<>("You can't modify this course", HttpStatus.FORBIDDEN);
        }
        try {
            materialEntity.setTitle(updateMaterialDto.getTitle());
            materialEntity.setDescription(updateMaterialDto.getDescription());
            materialEntity.setUrl(updateMaterialDto.getUrl());
            materialRepository.save(materialEntity);
            return new ResponseEntity<>("Material updated", HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Unable to update material", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMaterial(
            @PathVariable Long id,
            @AuthenticationPrincipal UserEntity user
    ) {
        Material materialEntity = materialService.getById(id);
        if (!RoleBasedAccessUtils.canModifyCourse(user, materialEntity.getModule().getCourse())) {
            return new ResponseEntity<>("You can't modify this course", HttpStatus.FORBIDDEN);
        }
        try {
            materialRepository.delete(materialEntity);
            return new ResponseEntity<>("Material deleted", HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Unable to delete material", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
