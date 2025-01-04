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
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/courses/**").hasAnyRole("USER", "TEACHER", "ADMIN")
                        .requestMatchers("/courses/new").hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers("/classes/**").hasAnyRole("USER", "TEACHER", "ADMIN")
                        .requestMatchers("/classes/new").hasRole("ADMIN")
                        .requestMatchers("/schools/**").hasAnyRole("USER", "TEACHER", "ADMIN")
                        .requestMatchers("/schools/new").hasRole("ADMIN")
                        .requestMatchers("/schedules").hasAnyRole("ADMIN")
                        .requestMatchers("/schedules/new").hasAnyRole("ADMIN")  
                        .requestMatchers("/schedules/{id}").hasAnyRole("USER", "TEACHER", "ADMIN")  
                        .requestMatchers(HttpMethod.PUT, "/schedules/{id}").hasAnyRole("ADMIN")  
                        .requestMatchers(HttpMethod.DELETE, "/schedules/{id}").hasRole("ADMIN")  
                        .requestMatchers("/actuator/**").permitAll()
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
