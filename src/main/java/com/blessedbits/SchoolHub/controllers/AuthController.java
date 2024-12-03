package com.blessedbits.SchoolHub.controllers;

import com.blessedbits.SchoolHub.dto.AuthResponseDto;
import com.blessedbits.SchoolHub.dto.LoginDto;
import com.blessedbits.SchoolHub.dto.RegisterDto;
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
        if (userRepository.existsByUsername(request.getUsername())) {
            return new ResponseEntity<>("Username already taken!", HttpStatus.BAD_REQUEST);
            
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            return new ResponseEntity<>("Email already taken!", HttpStatus.BAD_REQUEST);
            
        }
        UserEntity user = new UserEntity();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        Role roles = roleRepository.findByName("USER").get();
        user.setRoles(Collections.singletonList(roles));
    
        userRepository.save(user);
        if(!request.getEmail().isEmpty())
        {
            String token = UUID.randomUUID().toString();
            VerificationToken verificationToken = new VerificationToken();
            verificationToken.setToken(token);
            verificationToken.setUser(user);
            verificationToken.setExpiryDate(LocalDateTime.now().plusMinutes(15)); 
            tokenRepository.save(verificationToken);
            try{
                emailService.sendEmail(request.getEmail(),"Please verify your email", emailService.buildVerificationEmail(user.getUsername(), token));
            } catch (Exception e) {
                return new ResponseEntity<>(("Failed to send verification token\n" + e), HttpStatus.CONFLICT);
            }
            return new ResponseEntity<>("User registered successfully! Please check your email for verification.", HttpStatus.CREATED);
        }
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
        user.setEmail(verificationToken.getEmail());
        userRepository.save(user);
    
        tokenRepository.delete(verificationToken);
        return new ResponseEntity<>("Email verified successfully!", HttpStatus.OK);
}

    @GetMapping("test")
    public String hello()
    {
        return "hello";
    }
}
