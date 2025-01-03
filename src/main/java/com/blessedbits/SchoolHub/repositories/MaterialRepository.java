package com.blessedbits.SchoolHub.repositories;

import com.blessedbits.SchoolHub.models.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
}
