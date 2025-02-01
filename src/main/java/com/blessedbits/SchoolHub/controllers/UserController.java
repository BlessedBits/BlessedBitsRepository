package com.blessedbits.SchoolHub.controllers;

import com.blessedbits.SchoolHub.dto.UpdateInfoDto;
import com.blessedbits.SchoolHub.dto.UpdateNameDto;
import com.blessedbits.SchoolHub.dto.UserProfileDto;
import com.blessedbits.SchoolHub.misc.CloudFolder;
import com.blessedbits.SchoolHub.models.Submission;
import com.blessedbits.SchoolHub.models.UserEntity;
import com.blessedbits.SchoolHub.models.VerificationToken;
import com.blessedbits.SchoolHub.repositories.SubmissionRepository;
import com.blessedbits.SchoolHub.repositories.UserRepository;
import com.blessedbits.SchoolHub.repositories.VerificationTokenRepository;
import com.blessedbits.SchoolHub.services.EmailService;
import com.blessedbits.SchoolHub.services.StorageService;
import com.blessedbits.SchoolHub.services.UserService;
import jakarta.validation.Valid;

import org.apache.http.protocol.HTTP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @Autowired
    public UserController(UserRepository userRepository,
                          VerificationTokenRepository verificationTokenRepository,
                          UserService userService, EmailService emailService,
                          StorageService storageService, SubmissionRepository submissionRepository) {
        this.userRepository = userRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.userService = userService;
        this.emailService = emailService;
        this.storageService = storageService;
        this.submissionRepository = submissionRepository;
    }

    @PostMapping("/update-info")
    public ResponseEntity<String> updateUserInfo(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody @Valid UpdateInfoDto updateInfoDto)
    {
        UserEntity user = userService.getUserFromHeader(authorizationHeader);
        String email = updateInfoDto.getEmail();
        String username = updateInfoDto.getUsername();
        if(username != null && !username.isEmpty())
        {
            if (userRepository.existsByUsername(username))
            {
                return new ResponseEntity<>("Username already taken!", HttpStatus.BAD_REQUEST);
            }
            user.setUsername(username);
        }
        if(email != null && !email.isEmpty())
        {
            if(userRepository.existsByEmail(email))
            {
                return new ResponseEntity<>("Email already taken!", HttpStatus.BAD_REQUEST);
            }
            user.setEmail(email);
            String token = UUID.randomUUID().toString();
            VerificationToken verificationToken = new VerificationToken();
            verificationToken.setToken(token);
            verificationToken.setUser(user);
            verificationToken.setExpiryDate(LocalDateTime.now().plusMinutes(15));
            verificationTokenRepository.save(verificationToken);
            try{
                emailService.sendEmail(email,"Please verify your email", emailService.buildConfirmEmail(user.getUsername(), token));
            } catch (Exception e) {
                return new ResponseEntity<>(("Failed to send verification token\n" + e), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity<>("User info was updated successfully! Please check your email for verification.", HttpStatus.CREATED);
        }
        userRepository.save(user);
        return new ResponseEntity<>("User info was updated successfully!", HttpStatus.CREATED);
    }

    @PostMapping("/update-profile-image")
    public ResponseEntity<String> updateProfileImage(@RequestParam MultipartFile profileImage,
                                                     @RequestHeader("Authorization") String authorizationHeader) {
        UserEntity user = userService.getUserFromHeader(authorizationHeader);
        try {
            String url = storageService.uploadFile(profileImage, CloudFolder.PROFILE_IMAGES);
            user.setProfileImage(url);
            userRepository.save(user);
            return new ResponseEntity<>("Image updated", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update-name/{id}")
    public ResponseEntity<String> updateName(@PathVariable Integer id, @RequestBody UpdateNameDto updateNameDto) 
    {
        Optional<UserEntity> userOpt = userRepository.findById(id);
        if(userOpt.isEmpty())
        {
            return new ResponseEntity<>("Error: User is not found by provided ID.", HttpStatus.NOT_FOUND);
        }
        UserEntity user = userOpt.get(); 
        user.setFirstName(updateNameDto.getFirstName());
        user.setLastName(updateNameDto.getLastName());  
        try 
        {
            userRepository.save(user);
            return new ResponseEntity<>("User's name was successfully updated.", HttpStatus.OK);
        }
        catch(Exception e)
        {
            return new ResponseEntity<>(("Error: User's name was not updated." + e), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/grades")
    public ResponseEntity<List<Submission>> getGrades(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestHeader("Authorization") String authorizationHeader) {
        UserEntity user = userService.getUserFromHeader(authorizationHeader);
        try {
            List<Submission> submissions = submissionRepository.findSubmissionsByStudentIdAndDateRange(
                    user.getId(), startDate.atStartOfDay(), endDate.atStartOfDay()
            );
            return new ResponseEntity<>(submissions, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/set-duty/{id}")
    public ResponseEntity<String> setUsersDuty(@PathVariable Integer id, @RequestBody String duty) 
    {
        Optional<UserEntity> userOpt = userRepository.findById(id);
        if(userOpt.isEmpty())
        {
            return new ResponseEntity<>("Error: User is not found by provided ID.", HttpStatus.NOT_FOUND);
        }
        UserEntity user = userOpt.get();
        user.setDuty(duty);
        try 
        {
            userRepository.save(user);
            return new ResponseEntity<>("User's duty was successfully updated.", HttpStatus.OK);
        }
        catch(Exception e)
        {
            return new ResponseEntity<>(("Error: User's duty was not updated." + e), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfileInfo(@RequestHeader("Authorization") String authorizationHeader)
    {
        UserEntity user = userService.getUserFromHeader(authorizationHeader);
        try{
            return new ResponseEntity<UserProfileDto>(userService.getUserProfileInfo(user), HttpStatus.OK);
        }catch (Exception e)
        {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
    }

    @GetMapping("/role")
    public ResponseEntity<String> getUserRole(@RequestHeader("Authorization") String authorizationHeader) 
    {
        UserEntity user = userService.getUserFromHeader(authorizationHeader);
        try
        {
            return new ResponseEntity<String>(user.getRoles().get(0).getName(), HttpStatus.OK);
        }catch (Exception e)
        {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/school-id")
    public ResponseEntity<?> getUserSchoolId(@RequestHeader("Authorization") String authorizationHeader) 
    {
        UserEntity user = userService.getUserFromHeader(authorizationHeader);
        try 
        {
            Integer id = user.getSchool() != null ? user.getSchool().getId() : null;
            if(id == null)
            {
                return new ResponseEntity<>("Error: User don't have any school.", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(id, HttpStatus.OK);
        }catch (Exception e)
        {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    

}
