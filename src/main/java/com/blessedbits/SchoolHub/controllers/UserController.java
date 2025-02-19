package com.blessedbits.SchoolHub.controllers;

import com.blessedbits.SchoolHub.dto.RoleUpdateRequest;
import com.blessedbits.SchoolHub.dto.UpdateDutyDto;
import com.blessedbits.SchoolHub.dto.UpdateInfoDto;
import com.blessedbits.SchoolHub.dto.UpdateNameDto;
import com.blessedbits.SchoolHub.dto.UserProfileDto;
import com.blessedbits.SchoolHub.misc.CloudFolder;
import com.blessedbits.SchoolHub.misc.RoleBasedAccessUtils;
import com.blessedbits.SchoolHub.misc.RoleType;
import com.blessedbits.SchoolHub.models.Submission;
import com.blessedbits.SchoolHub.models.UserEntity;
import com.blessedbits.SchoolHub.models.VerificationToken;
import com.blessedbits.SchoolHub.projections.dto.SubmissionDto;
import com.blessedbits.SchoolHub.projections.dto.UserDto;
import com.blessedbits.SchoolHub.projections.mappers.BasicDtoMapper;
import com.blessedbits.SchoolHub.projections.mappers.UserMapper;
import com.blessedbits.SchoolHub.repositories.SubmissionRepository;
import com.blessedbits.SchoolHub.repositories.UserRepository;
import com.blessedbits.SchoolHub.repositories.VerificationTokenRepository;
import com.blessedbits.SchoolHub.services.EmailService;
import com.blessedbits.SchoolHub.services.StorageService;
import com.blessedbits.SchoolHub.services.SubmissionService;
import com.blessedbits.SchoolHub.services.UserService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final UserService userService;
    private final EmailService emailService;
    private final StorageService storageService;
    private final SubmissionRepository submissionRepository;
    private final RoleBasedAccessUtils roleBasedAccessUtils;
    private final SubmissionService submissionService;

    @Autowired
    public UserController(
            UserRepository userRepository,
            VerificationTokenRepository verificationTokenRepository,
            UserService userService, EmailService emailService,
            StorageService storageService, SubmissionRepository submissionRepository,
            RoleBasedAccessUtils roleBasedAccessUtils,
            SubmissionService submissionService
    ) {
        this.userRepository = userRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.userService = userService;
        this.emailService = emailService;
        this.storageService = storageService;
        this.submissionRepository = submissionRepository;
        this.roleBasedAccessUtils = roleBasedAccessUtils;
        this.submissionService = submissionService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(
            @PathVariable Integer id,
            @RequestParam(required = false) List<String> include,
            @AuthenticationPrincipal UserEntity user
    ) {
        UserEntity loadedUser = userService.getLoadedByIdOrUsername(id, user.getUsername(), include);
        if (roleBasedAccessUtils.canAccessUser(user, loadedUser)) {
            return new ResponseEntity<>(UserMapper.INSTANCE.toUserDto(loadedUser, include), HttpStatus.OK);
        }
        return new ResponseEntity<>(BasicDtoMapper.toUserDto(loadedUser), HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<UserDto> getUser(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) List<String> include,
            @AuthenticationPrincipal UserEntity user
    ) {
        if (username != null) {
            UserEntity loadedUser = userService.getLoadedByUsername(username, include);
            if (roleBasedAccessUtils.canAccessUser(user, loadedUser)) {
                return new ResponseEntity<>(UserMapper.INSTANCE.toUserDto(loadedUser, include), HttpStatus.OK);
            }
            return new ResponseEntity<>(BasicDtoMapper.toUserDto(loadedUser), HttpStatus.OK);
        }
        if (email != null) {
            UserEntity loadedUser = userService.getLoadedByEmail(email, include);
            if (roleBasedAccessUtils.canAccessUser(user, loadedUser)) {
                return new ResponseEntity<>(UserMapper.INSTANCE.toUserDto(loadedUser, include), HttpStatus.OK);
            }
            return new ResponseEntity<>(BasicDtoMapper.toUserDto(loadedUser), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/{id}/info")
    public ResponseEntity<String> updateUserInfo(
            @PathVariable Integer id,
            @RequestBody @Valid UpdateInfoDto updateInfoDto,
            @AuthenticationPrincipal UserEntity user
    ) {
        UserEntity targetUser = userService.getByIdOrUser(id, user);
        String email = updateInfoDto.getEmail();
        String username = updateInfoDto.getUsername();
        if (username != null && !username.isEmpty())
        {
            if (userRepository.existsByUsername(username))
            {
                return new ResponseEntity<>("Username already taken!", HttpStatus.BAD_REQUEST);
            }
            targetUser.setUsername(username);
        }
        if (email != null && !email.isEmpty())
        {
            if(userRepository.existsByEmail(email))
            {
                return new ResponseEntity<>("Email already taken!", HttpStatus.BAD_REQUEST);
            }
            targetUser.setEmail(email);
            String token = UUID.randomUUID().toString();
            VerificationToken verificationToken = new VerificationToken();
            verificationToken.setToken(token);
            verificationToken.setUser(targetUser);
            verificationToken.setExpiryDate(LocalDateTime.now().plusMinutes(15));
            verificationTokenRepository.save(verificationToken);
            try{
                emailService.sendEmail(
                        email,"Please verify your email",
                        emailService.buildConfirmEmail(targetUser.getUsername(), token));
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return new ResponseEntity<>("Failed to send verification token\n",
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity<>(
                    "User info was updated successfully! Please check your email for verification.",
                    HttpStatus.CREATED);
        }
        userRepository.save(targetUser);
        return new ResponseEntity<>("User info was updated successfully!", HttpStatus.CREATED);
    }

    @PutMapping("/{id}/image")
    public ResponseEntity<String> updateProfileImage(
            @PathVariable Integer id,
            @RequestParam MultipartFile profileImage,
            @AuthenticationPrincipal UserEntity user
    ) {
        UserEntity targetUser = userService.getByIdOrUser(id, user);
        if (!roleBasedAccessUtils.canModifyUser(user, targetUser)) {
            return new ResponseEntity<>("You can't modify this user", HttpStatus.FORBIDDEN);
        }
        try {
            if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
                storageService.deleteFile(user.getProfileImage());
            }
            String url = storageService.uploadFile(profileImage, CloudFolder.PROFILE_IMAGES);
            targetUser.setProfileImage(url);
            userRepository.save(targetUser);
            return new ResponseEntity<>("Image updated", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}/duty")
    public ResponseEntity<String> setUsersDuty(
            @PathVariable Integer id,
            @RequestBody UpdateDutyDto updateDutyDto,
            @AuthenticationPrincipal UserEntity user
    ) {
        UserEntity targetUser = userService.getByIdOrUser(id, user);
        if (!roleBasedAccessUtils.canModifyUser(user,  targetUser)) {
            return new ResponseEntity<>("You can't modify this user", HttpStatus.FORBIDDEN);
        }
        targetUser.setDuty(updateDutyDto.getDuty());
        try {
            userRepository.save(targetUser);
            return new ResponseEntity<>("User's duty was successfully updated.", HttpStatus.OK);
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Unable to update user duty.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserEntity user
    ) {
        UserEntity targetUser = userService.getByIdOrUser(id, user);
        if (!roleBasedAccessUtils.canModifyUser(user, targetUser)) {
            return new ResponseEntity<>("You can't modify this user", HttpStatus.FORBIDDEN);
        }
        try {
            userRepository.delete(targetUser);
            return new ResponseEntity<>("User deleted", HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Unable to delete user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}/name")
    public ResponseEntity<String> updateName(
            @PathVariable Integer id,
            @RequestBody UpdateNameDto updateNameDto,
            @AuthenticationPrincipal UserEntity user
    ) {
        UserEntity targetUser = userService.getByIdOrUser(id, user);
        if (!roleBasedAccessUtils.canModifyUser(user, targetUser)) {
            return new ResponseEntity<>("You can't modify this user", HttpStatus.FORBIDDEN);
        }
        String firstName = updateNameDto.getFirstName();
        String lastName = updateNameDto.getLastName();
        if (firstName != null && !firstName.isEmpty()) {
            targetUser.setFirstName(firstName);
        }
        if (lastName != null && !lastName.isEmpty()) {
            targetUser.setLastName(lastName);
        }
        try {
            userRepository.save(user);
            return new ResponseEntity<>("User's name was successfully updated.", HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>("Unable to update user's name", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<String> updateRole(
            @PathVariable Integer id,
            @RequestBody RoleUpdateRequest roleUpdateRequest,
            @AuthenticationPrincipal UserEntity user
    ) {
        UserEntity targetUser = userService.getByIdOrUser(id, user);
        RoleType targetRole = roleUpdateRequest.getRole();
        if (!roleBasedAccessUtils.canModifyUserRole(user, targetUser, targetRole)) {
            return new ResponseEntity<>("You are not allowed to modify this user's role", HttpStatus.FORBIDDEN);
        }
        targetUser.setRole(roleUpdateRequest.getRole());
        try {
            userRepository.save(targetUser);
            return new ResponseEntity<>("Role updated successfully.", HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Unable to update user's role", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}/grades")
    public ResponseEntity<List<SubmissionDto>> getGrades(
            @PathVariable Integer id,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal UserEntity user) {
        UserEntity targetUser = userService.getByIdOrUser(id, user);
        if (!roleBasedAccessUtils.canAccessUser(user, targetUser)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            List<Submission> submissions = submissionRepository.findSubmissionsByStudentIdAndDateRange(
                    targetUser.getId(), startDate.atStartOfDay(), endDate.atStartOfDay()
            );
            return new ResponseEntity<>(submissionService.mapAllToDto(submissions, null), HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
