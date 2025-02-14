package com.blessedbits.SchoolHub.services;

import com.blessedbits.SchoolHub.models.Role;
import com.blessedbits.SchoolHub.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
}
