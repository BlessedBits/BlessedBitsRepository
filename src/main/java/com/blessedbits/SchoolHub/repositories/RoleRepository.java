package com.blessedbits.SchoolHub.repositories;

import com.blessedbits.SchoolHub.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> 
{
    Optional<Role> findByName(String name);
    List<Role> findAll();
}
