package com.blessedbits.SchoolHub.repositories;

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

    @EntityGraph(attributePaths = {"roles"})
    @Query("select u from UserEntity u where u.username=:username")
    Optional<UserEntity> findWithRolesByUsername(@Param("username") String username);

    @EntityGraph(attributePaths = {"roles"})
    @Query("select u from UserEntity u where u.username=:username")
    <T> T findWithRolesByUsername(@Param("username") String username, Class<T> clazz);

    Boolean existsByEmail(String email);

    Boolean existsByUsername(String username);

    @Query("SELECT COUNT(u) FROM UserEntity u JOIN u.roles r WHERE u.school.id = :schoolId AND r.name = :role")
    long countBySchoolIdAndRole(int schoolId, String role);

    @Query("SELECT u FROM UserEntity u JOIN u.roles r WHERE u.school.id = :schoolId AND r.name = 'TEACHER'")
    List<UserEntity> findTeachersBySchoolId(int schoolId);
}
