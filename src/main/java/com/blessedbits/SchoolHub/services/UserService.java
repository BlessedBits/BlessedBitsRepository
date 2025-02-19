package com.blessedbits.SchoolHub.services;

import com.blessedbits.SchoolHub.misc.EntityManagerUtils;
import com.blessedbits.SchoolHub.models.UserEntity;
import com.blessedbits.SchoolHub.projections.dto.UserDto;
import com.blessedbits.SchoolHub.projections.mappers.UserMapper;
import com.blessedbits.SchoolHub.dto.UserProfileDto;
import com.blessedbits.SchoolHub.repositories.UserRepository;
import com.blessedbits.SchoolHub.security.JWTUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final JWTUtils jwtUtils;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public UserService(UserRepository userRepository, JWTUtils jwtUtils) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
    }

    public UserEntity getUserFromHeader(String authHeader) {
        String accessToken = jwtUtils.getJwtFromHeader(authHeader);
        String username = jwtUtils.getUsernameFromJWT(accessToken);
        return getByUsername(username);
    }

    public UserEntity getById(Integer id) {
        return userRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with given id not found")
        );
    }

    public UserEntity getByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with given username not found")
        );
    }

    public UserEntity getByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with given email not found")
        );
    }

    public UserEntity getByIdOrUser(Integer id, UserEntity user) {
        if (id != 0) {
            return getById(id);
        }
        return user;
    }

    public UserEntity getLoadedByIdOrUsername(Integer id, String username, List<String> include) {
        if (id != 0) {
            return getLoadedById(id, include);
        }
        return getLoadedByUsername(username, include);
    }

    public <T> T getByUsername(String username, Class<T> clazz) {
        T user = userRepository.findByUsername(username, clazz);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with given username not found");
        }
        return user;
    }

    public UserEntity getLoadedById(Integer id, List<String> include) {
        String jpql = "select u from UserEntity u where u.id = :id";
        TypedQuery<UserEntity> query = EntityManagerUtils
                .createTypedQueryWithGraph(UserEntity.class, entityManager, jpql, include);
        query.setParameter("id", id);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with given id not found");
        } catch (Exception e) {
            System.out.println("Couldn't execute query with entity graph");
            return getById(id);
        }
    }

    public UserEntity getLoadedByUsername(String username, List<String> include) {
        String jpql = "select u from UserEntity u where u.username = :username";
        TypedQuery<UserEntity> query = EntityManagerUtils
                .createTypedQueryWithGraph(UserEntity.class, entityManager, jpql, include);
        query.setParameter("username", username);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with given username not found");
        } catch (Exception e) {
            System.out.println("Couldn't execute query with entity graph");
            return getByUsername(username);
        }
    }

    public UserEntity getLoadedByEmail(String email, List<String> include) {
        String jpql = "select u from UserEntity u where u.email = :email";
        TypedQuery<UserEntity> query = EntityManagerUtils
                .createTypedQueryWithGraph(UserEntity.class, entityManager, jpql, include);
        query.setParameter("email", email);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with given email not found");
        } catch (Exception e) {
            System.out.println("Couldn't execute query with entity graph");
            return getByEmail(email);
        }
    }

    public List<UserEntity> getAllLoaded(List<String> include) {
        String jpql = "select u from UserEntity u";
        TypedQuery<UserEntity> query = EntityManagerUtils
                .createTypedQueryWithGraph(UserEntity.class, entityManager, jpql, include);
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No results found for the query");
        } catch (Exception e) {
            System.out.println("Couldn't execute query with entity graph");
            return userRepository.findAll();
        }
    }

    public List<UserDto> getAllAsDto(List<String> include) {
        return userRepository.findAll().stream()
                .map(userEntity -> UserMapper.INSTANCE.toUserDto(userEntity, include))
                .toList();
    }

    public List<UserDto> mapAllToDto(Set<UserEntity> users, List<String> include) {
        return users.stream()
                .map(userEntity -> UserMapper.INSTANCE.toUserDto(userEntity, include))
                .toList();
    }

    public List<UserDto> mapAllToDto(List<UserEntity> users, List<String> include) {
        return users.stream()
                .map(userEntity -> UserMapper.INSTANCE.toUserDto(userEntity, include))
                .toList();
    }

    public UserProfileDto getUserProfileInfo(UserEntity user)
    {
        UserProfileDto userProfileDto = new UserProfileDto();
        userProfileDto.setFirstName(user.getFirstName());
        userProfileDto.setLastName(user.getLastName());
        userProfileDto.setEmail(user.getEmail());
        userProfileDto.setRole(user.getRole() == null ? "No role" : String.valueOf(user.getRole()));
        userProfileDto.setDuty(user.getDuty());
        userProfileDto.setProfileImage(user.getProfileImage());
        userProfileDto.setSchool(user.getSchool() != null ? user.getSchool().getName() : "No school");
        return userProfileDto;
    }
 
}