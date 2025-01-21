package com.blessedbits.SchoolHub.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private JwtAuthEntryPoint authEntryPoint;

    @Autowired
    public SecurityConfig(JwtAuthEntryPoint authEntryPoint) {
        this.authEntryPoint = authEntryPoint;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authEntryPoint)
                )
                .sessionManagement(sm -> sm
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/auth/**").permitAll()

                        .requestMatchers("/courses/**").hasAnyRole(
                                "STUDENT", "TEACHER", "SCHOOL_ADMIN")
                        .requestMatchers("/courses/new").hasAnyRole(
                                "TEACHER", "SCHOOL_ADMIN")

                        .requestMatchers("/classes/**").hasAnyRole(
                                "STUDENT", "TEACHER", "SCHOOL_ADMIN")
                        .requestMatchers("/classes/new").hasRole("SCHOOL_ADMIN")

                        .requestMatchers("/schools/**").hasAnyRole(
                                "USER", "STUDENT", "TEACHER", "SCHOOL_ADMIN", "PLATFORM_ADMIN")
                        .requestMatchers("/schools/new").hasRole("PLATFORM_ADMIN")

                        .requestMatchers("/users/**").hasAnyRole(
                                "USER", "STUDENT", "TEACHER", "SCHOOL_ADMIN", "PLATFORM_ADMIN")

                        .requestMatchers("/schedules/**").hasAnyRole(
                                "STUDENT", "TEACHER", "SCHOOL_ADMIN")
                        .requestMatchers("/schedules/new").hasRole("SCHOOL_ADMIN")  
                        .requestMatchers(HttpMethod.GET, "/schedules/{id}").hasAnyRole("STUDENT", "TEACHER", "SCHOOL_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/schedules/{id}").hasRole("SCHOOL_ADMIN")  
                        .requestMatchers(HttpMethod.DELETE, "/schedules/{id}").hasRole("SCHOOL_ADMIN")  

                        .requestMatchers("/news/**").hasAnyRole(
                                "STUDENT", "TEACHER", "SCHOOL_ADMIN", "PLATFORM_ADMIN")
                        .requestMatchers("/news/new").hasRole("SCHOOL_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/news/{id}").hasRole("SCHOOL_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/news/{id}").hasRole("SCHOOL_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/news/{id}").hasRole("SCHOOL_ADMIN")
                )
                .httpBasic(Customizer.withDefaults());
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JWTAuthenticationFilter jwtAuthenticationFilter() {
        return new JWTAuthenticationFilter();
    }

}
