package com.blessedbits.SchoolHub.repositories;

import com.blessedbits.SchoolHub.misc.RoleType;
import com.blessedbits.SchoolHub.models.UserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    
    Optional<UserEntity> findByUsername(String username);
    <T> T findByUsername(String username, Class<T> clazz);

    Optional<UserEntity> findByEmail(String email);

    Boolean existsByEmail(String email);

    Boolean existsByUsername(String username);

    @Query("SELECT COUNT(u) FROM UserEntity u WHERE u.school.id = :schoolId AND u.role = :role")
    long countBySchoolIdAndRole(int schoolId, RoleType role);

    @Query("SELECT u FROM UserEntity u WHERE u.school.id = :schoolId AND u.role = :role")
    List<UserEntity> findBySchoolIdAndRole(int schoolId, RoleType role);
}
