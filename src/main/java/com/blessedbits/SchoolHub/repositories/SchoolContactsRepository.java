package com.blessedbits.SchoolHub.repositories;

import com.blessedbits.SchoolHub.models.SchoolContacts;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SchoolContactsRepository extends JpaRepository<SchoolContacts, Integer> {
    Optional<SchoolContacts> findBySchoolId(int schoolId);
}
