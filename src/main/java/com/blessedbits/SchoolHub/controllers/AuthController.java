package com.blessedbits.SchoolHub.controllers;

import com.blessedbits.SchoolHub.dto.AuthResponseDto;
import com.blessedbits.SchoolHub.dto.LoginDto;
import com.blessedbits.SchoolHub.dto.RegisterDto;
import com.blessedbits.SchoolHub.dto.UsernameDto;
import com.blessedbits.SchoolHub.dto.ChangePasswordDto;
import com.blessedbits.SchoolHub.models.Role;
import com.blessedbits.SchoolHub.models.UserEntity;
import com.blessedbits.SchoolHub.models.VerificationToken;
import com.blessedbits.SchoolHub.repositories.RoleRepository;
import com.blessedbits.SchoolHub.repositories.UserRepository;
import com.blessedbits.SchoolHub.repositories.VerificationTokenRepository;
import com.blessedbits.SchoolHub.security.JWTGenerator;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.blessedbits.SchoolHub.services.EmailService;



@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private VerificationTokenRepository tokenRepository;
    private JWTGenerator jwtGenerator;
    private EmailService emailService;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository,
                          VerificationTokenRepository tokenRepository, JWTGenerator jwtGenerator,
                          EmailService emailService, RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.jwtGenerator = jwtGenerator;
        this.emailService = emailService;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(),
                        loginDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtGenerator.generateJWT(authentication);
        return new ResponseEntity<>(new AuthResponseDto(token), HttpStatus.OK);
    }

    @PostMapping("register")
    public ResponseEntity<String> register(@RequestBody RegisterDto request) {
        if (userRepository.existsByUsername(request.getUsername())) 
        {
            return new ResponseEntity<>("Username already taken!", HttpStatus.BAD_REQUEST);
            
        }
        if ((request.getEmail() != null && !request.getEmail().isEmpty()) && userRepository.existsByEmail(request.getEmail())) 
        {
            return new ResponseEntity<>("Email already taken!", HttpStatus.BAD_REQUEST); 
        }
        UserEntity user = new UserEntity();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        Role roles = roleRepository.findByName("USER").get();
        user.setRoles(Collections.singletonList(roles));
        if(!request.getEmail().isEmpty() && request.getEmail() != null)
        {
            user.setEmail(request.getEmail());
            userRepository.save(user);
            String token = UUID.randomUUID().toString();
            VerificationToken verificationToken = new VerificationToken();
            verificationToken.setToken(token);
            verificationToken.setUser(user);
            verificationToken.setExpiryDate(LocalDateTime.now().plusMinutes(15)); 
            tokenRepository.save(verificationToken);
            try{
                emailService.sendEmail(request.getEmail(),"Please verify your email", emailService.buildConfirmEmail(user.getUsername(), token));
            } catch (Exception e) {
                return new ResponseEntity<>(("Failed to send verification token\n" + e), HttpStatus.CONFLICT);
            }
            return new ResponseEntity<>("User registered successfully! Please check your email for verification.", HttpStatus.CREATED);
        }
        userRepository.save(user);
        return new ResponseEntity<>("User registered successfully!", HttpStatus.CREATED);
    }

    @GetMapping("confirm")
    public ResponseEntity<String> confirmEmail(@RequestParam("token") String token) {
        Optional<VerificationToken> verificationTokenOptional = tokenRepository.findByToken(token);
        if (!verificationTokenOptional.isPresent())
        {
            return new ResponseEntity<>("Invalid token!", HttpStatus.CONFLICT);
        }
        VerificationToken verificationToken = verificationTokenOptional.get();
        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(verificationToken); 
            return new ResponseEntity<>("Token has expired!", HttpStatus.CONFLICT);
        }
    
        UserEntity user = verificationToken.getUser();
        user.setIsConfirmed(true);
        userRepository.save(user);
    
        tokenRepository.delete(verificationToken);
        return new ResponseEntity<>("Email verified successfully!", HttpStatus.OK);
}

    @PostMapping("reset-password-request")
    public ResponseEntity<String> resetPasswordRequest(@RequestBody UsernameDto usernameDto) {
        Optional<UserEntity> userOptional = userRepository.findByUsername(usernameDto.getUsername());
        if (!userOptional.isPresent()) 
        {
            return new ResponseEntity<>("User not found!", HttpStatus.NOT_FOUND);
        }
        UserEntity user = userOptional.get();
        if (user.getEmail() == null || user.getEmail().isEmpty()) 
        {
            return new ResponseEntity<>("Please add an email to your profile to reset your password.", HttpStatus.BAD_REQUEST);
        }
        if (!user.getIsConfirmed())
        {
            return new ResponseEntity<>("Please verify your email.", HttpStatus.CONFLICT);
        }
        String token = UUID.randomUUID().toString();
        VerificationToken resetToken = new VerificationToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(15)); 
        tokenRepository.save(resetToken);
        try {
            emailService.sendEmail(user.getEmail(), "Password Reset Request", 
                emailService.buildResetPasswordEmail(user.getUsername(), token)); 
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to send reset password email", HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>("Password reset email has been sent.", HttpStatus.OK);
    }

    @PostMapping("reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam("token") String token, 
                                                @RequestParam("newPassword") String newPassword) {
        Optional<VerificationToken> resetTokenOptional = tokenRepository.findByToken(token);
        if (resetTokenOptional.isEmpty()) {
            return new ResponseEntity<>("Invalid or expired token", HttpStatus.BAD_REQUEST);
        }

        VerificationToken resetToken = resetTokenOptional.get();
        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(resetToken); 
            return new ResponseEntity<>("Token has expired", HttpStatus.BAD_REQUEST);
        }

        UserEntity user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));  
        userRepository.save(user);
        
        tokenRepository.delete(resetToken);  

        return new ResponseEntity<>("Password has been successfully reset", HttpStatus.OK);
    }

    @PostMapping("change-password")
    public ResponseEntity<String> changePassword(@RequestHeader("Authorization") String authorizationHeader, @RequestBody ChangePasswordDto changePasswordDto) 
    {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) 
        {
            return new ResponseEntity<>("Authorization header is missing or invalid", HttpStatus.UNAUTHORIZED);
        }
        String token = authorizationHeader.substring(7);
        String username = jwtGenerator.getUsernameFromJWT(token);
        Optional<UserEntity> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) 
        {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        UserEntity user = userOptional.get();
        if (!passwordEncoder.matches(changePasswordDto.getOldPassword(), user.getPassword())) 
        {
            return new ResponseEntity<>("Old password is incorrect", HttpStatus.BAD_REQUEST);
        }
        if (!changePasswordDto.getNewPassword().equals(changePasswordDto.getConfirmPassword())) 
        {
            return new ResponseEntity<>("New passwords do not match", HttpStatus.BAD_REQUEST);
        }
        user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
        userRepository.save(user);

        return new ResponseEntity<>("Password changed successfully", HttpStatus.OK);
    }

    @GetMapping("test")
    public String hello()
    {
        return "hello";
    }
}
