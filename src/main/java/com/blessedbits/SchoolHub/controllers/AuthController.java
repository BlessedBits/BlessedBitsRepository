package com.blessedbits.SchoolHub.controllers;

import com.blessedbits.SchoolHub.dto.AuthResponseDto;
import com.blessedbits.SchoolHub.dto.LoginDto;
import com.blessedbits.SchoolHub.dto.RegisterDto;
import com.blessedbits.SchoolHub.dto.UsernameDto;
import com.blessedbits.SchoolHub.dto.ChangePasswordDto;
import com.blessedbits.SchoolHub.misc.RoleBasedAccessUtils;
import com.blessedbits.SchoolHub.misc.RoleType;
import com.blessedbits.SchoolHub.models.UserEntity;
import com.blessedbits.SchoolHub.models.VerificationToken;
import com.blessedbits.SchoolHub.projections.dto.UserDto;
import com.blessedbits.SchoolHub.projections.mappers.UserMapper;
import com.blessedbits.SchoolHub.repositories.UserRepository;
import com.blessedbits.SchoolHub.repositories.VerificationTokenRepository;
import com.blessedbits.SchoolHub.security.JWTUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.blessedbits.SchoolHub.security.SecurityConstants;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.blessedbits.SchoolHub.services.EmailService;
import com.blessedbits.SchoolHub.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;
    private final JWTUtils jwtUtils;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final RoleBasedAccessUtils roleBasedAccessUtils;
    private final UserService userService;

    public AuthController(
            AuthenticationManager authenticationManager, UserRepository userRepository,
            VerificationTokenRepository tokenRepository, JWTUtils jwtUtils,
            EmailService emailService, PasswordEncoder passwordEncoder,
            RoleBasedAccessUtils roleBasedAccessUtils,
            UserService userService
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.jwtUtils = jwtUtils;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.roleBasedAccessUtils = roleBasedAccessUtils;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginDto loginDto, @RequestParam(required = false, defaultValue = "false") Boolean remember) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(),
                        loginDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtUtils.generateAccessJWT(authentication.getName());

        ResponseCookie cookie;

        if (remember) {
            String refreshToken = jwtUtils.generateRefreshJWT(authentication.getName());
            cookie = ResponseCookie.from("refreshToken", refreshToken)
                    .maxAge(SecurityConstants.REFRESH_TOKEN_VALIDITY/1000)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .sameSite("None")
                    .build();
        }
        else {
            String refreshToken = jwtUtils.generateShortRefreshJWT(authentication.getName());
            cookie = ResponseCookie.from("refreshToken", refreshToken)
                    .maxAge(SecurityConstants.REFRESH_TOKEN_VALIDITY/1000/30)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .sameSite("None")
                    .build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new AuthResponseDto(accessToken));
    }

    @GetMapping("/token/refresh")
    public ResponseEntity<AuthResponseDto> refreshJWT(@CookieValue(name = "refreshToken") String refreshToken) {
        if (refreshToken == null || !jwtUtils.validateJWT(refreshToken)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String username = jwtUtils.getUsernameFromJWT(refreshToken);
        String accessToken = jwtUtils.generateAccessJWT(username);
        return new ResponseEntity<>(new AuthResponseDto(accessToken), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid List<RegisterDto> users,
                                           @RequestParam RoleType role,
                                           @AuthenticationPrincipal UserEntity admin) 
    {
        final Integer passwordLength = 8;
        List<UserDto> createdUsers = new ArrayList<>();
        if(!roleBasedAccessUtils.canRegisterUser(admin))
        {
            return new ResponseEntity<List<UserDto>>(createdUsers, HttpStatus.FORBIDDEN);
        }
        for (RegisterDto user : users)
        {
            String baseUsername = userService.generateUsername(user.getFirstName(), user.getLastName());
            String uniqueUsername = userService.generateUniqueUsername(baseUsername);
            String password = userService.generateRandomPassword(passwordLength);
                
            UserEntity newUser = new UserEntity();
            newUser.setFirstName(user.getFirstName());
            newUser.setLastName(user.getLastName());
            newUser.setUsername(uniqueUsername);
            newUser.setPassword(passwordEncoder.encode(password));
            newUser.setRole(role);
            if(admin.getRole().equals(RoleType.SCHOOL_ADMIN))
            {
                newUser.setSchool(admin.getSchool());
            }
            try{
                userRepository.save(newUser);
            } catch(Exception e)
            {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
            UserDto createdUserDto = UserMapper.INSTANCE.toUserDto(newUser, null);
            createdUserDto.setPassword(password);
            createdUsers.add(createdUserDto);  
        }
        return new ResponseEntity<>(createdUsers, HttpStatus.CREATED);
    }

    @GetMapping("/confirm")
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

    @PostMapping("/reset-password-request")
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

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestParam("token") String token,
            @RequestParam("newPassword") String newPassword
    ) {
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

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @RequestBody ChangePasswordDto changePasswordDto,
            @AuthenticationPrincipal UserEntity user
    ) {
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
}
