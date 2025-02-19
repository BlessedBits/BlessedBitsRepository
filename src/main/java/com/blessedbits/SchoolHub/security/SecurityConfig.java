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
    private JwtAccessDeniedHandler accessDeniedHandler;

    @Autowired
    public SecurityConfig(JwtAuthEntryPoint authEntryPoint, JwtAccessDeniedHandler accessDeniedHandler) {
        this.authEntryPoint = authEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .sessionManagement(sm -> sm
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/auth/**").permitAll()

                        // CourseController
                        .requestMatchers(HttpMethod.GET, "/courses").hasRole("PLATFORM_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/courses").hasAnyRole(
                                "PLATFORM_ADMIN", "SCHOOL_ADMIN", "TEACHER"
                        )
                        .requestMatchers(HttpMethod.GET, "/courses/{id}").hasAnyRole(
                                "PLATFORM_ADMIN", "SCHOOL_ADMIN", "TEACHER", "STUDENT"
                        )
                        .requestMatchers(HttpMethod.PUT, "/courses/{id}").hasAnyRole(
                                "PLATFORM_ADMIN", "SCHOOL_ADMIN", "TEACHER"
                        )
                        .requestMatchers(HttpMethod.DELETE, "/courses/{id}").hasAnyRole(
                                "PLATFORM_ADMIN", "SCHOOL_ADMIN", "TEACHER"
                        )
                        .requestMatchers(HttpMethod.GET, "/courses/{id}/modules").hasAnyRole(
                                "PLATFORM_ADMIN", "SCHOOL_ADMIN", "TEACHER", "STUDENT"
                        )
                        .requestMatchers("/courses/**").hasAnyRole(
                                "STUDENT", "TEACHER", "SCHOOL_ADMIN", "PLATFORM_ADMIN")

                        // ClassController
                        .requestMatchers(HttpMethod.GET, "/classes").hasRole("PLATFORM_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/classes").hasAnyRole(
                                "PLATFORM_ADMIN", "SCHOOL_ADMIN"
                        )
                        .requestMatchers(HttpMethod.GET, "/classes/{id}").hasAnyRole(
                                "PLATFORM_ADMIN", "SCHOOL_ADMIN", "TEACHER", "STUDENT"
                        )
                        .requestMatchers(HttpMethod.PUT, "/classes/{id}").hasAnyRole(
                                "PLATFORM_ADMIN", "SCHOOL_ADMIN"
                        )
                        .requestMatchers(HttpMethod.DELETE, "/classes/{id}").hasAnyRole(
                                "PLATFORM_ADMIN", "SCHOOL_ADMIN"
                        )
                        .requestMatchers(HttpMethod.POST, "/classes/{id}/courses").hasAnyRole(
                                "PLATFORM_ADMIN", "SCHOOL_ADMIN"
                        )
                        .requestMatchers(HttpMethod.GET, "/classes/{id}/courses").hasAnyRole(
                                "PLATFORM_ADMIN", "SCHOOL_ADMIN", "TEACHER", "STUDENT"
                        )
                        .requestMatchers(HttpMethod.DELETE, "/classes/{id}/courses").hasAnyRole(
                                "PLATFORM_ADMIN", "SCHOOL_ADMIN"
                        )
                        .requestMatchers(HttpMethod.POST, "/classes/{id}/students").hasAnyRole(
                                "PLATFORM_ADMIN", "SCHOOL_ADMIN"
                        )
                        .requestMatchers(HttpMethod.GET, "/classes/{id}/students").hasAnyRole(
                                "PLATFORM_ADMIN", "SCHOOL_ADMIN", "TEACHER", "STUDENT"
                        )
                        .requestMatchers(HttpMethod.DELETE, "/classes/{id}/students").hasAnyRole(
                                "PLATFORM_ADMIN", "SCHOOL_ADMIN"
                        )
                        .requestMatchers(HttpMethod.GET, "/classes/{id}/schedules").hasAnyRole(
                                "PLATFORM_ADMIN", "SCHOOL_ADMIN", "TEACHER", "STUDENT"
                        )
                        .requestMatchers("/classes/**").hasAnyRole(
                                "STUDENT", "TEACHER", "SCHOOL_ADMIN")

                        // SchoolController
                        .requestMatchers(HttpMethod.GET, "/schools").hasRole("PLATFORM_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/schools").hasRole("PLATFORM_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/schools/{id}").hasAnyRole(
                                "PLATFORM_ADMIN", "SCHOOL_ADMIN", "TEACHER", "STUDENT", "USER"
                        )
                        .requestMatchers(HttpMethod.PUT, "/schools/{id}/info").hasAnyRole(
                                "PLATFORM_ADMIN", "SCHOOL_ADMIN"
                        )
                        .requestMatchers(HttpMethod.PUT, "/schools/{id}/logo").hasAnyRole(
                                "PLATFORM_ADMIN", "SCHOOL_ADMIN"
                        )
                        .requestMatchers(HttpMethod.DELETE, "/schools/{id}").hasAnyRole(
                                "PLATFORM_ADMIN", "SCHOOL_ADMIN"
                        )
                        .requestMatchers(HttpMethod.GET, "/schools/{id}/rating").hasAnyRole(
                                "PLATFORM_ADMIN", "SCHOOL_ADMIN", "TEACHER", "STUDENT", "USER"
                        )
                        .requestMatchers(HttpMethod.POST, "/schools/{id}/users").hasAnyRole(
                                "PLATFORM_ADMIN", "SCHOOL_ADMIN"
                        )
                        .requestMatchers(HttpMethod.GET, "/schools/{id}/users").hasAnyRole(
                                "PLATFORM_ADMIN", "SCHOOL_ADMIN", "TEACHER"
                        )
                        .requestMatchers(HttpMethod.GET, "/schools/{id}/classes").hasAnyRole(
                                "PLATFORM_ADMIN", "SCHOOL_ADMIN", "TEACHER", "STUDENT"
                        )
                        .requestMatchers(HttpMethod.GET, "/schools/{id}/courses").hasAnyRole(
                                "PLATFORM_ADMIN", "SCHOOL_ADMIN", "TEACHER"
                        )
                        .requestMatchers(HttpMethod.GET, "/schools/{id}/news").hasAnyRole(
                                "PLATFORM_ADMIN", "SCHOOL_ADMIN", "TEACHER", "STUDENT", "USER"
                        )
                        .requestMatchers(HttpMethod.GET, "/schools/contacts").hasAnyRole(
                                "USER", "STUDENT", "TEACHER", "SCHOOL_ADMIN", "PLATFORM_ADMIN"
                        )
                        .requestMatchers(HttpMethod.PUT, "/schools/update-contacts").hasAnyRole(
                                "SCHOOL_ADMIN", "PLATFORM_ADMIN"
                        )
                        .requestMatchers(HttpMethod.PUT, "/schools/update-info").hasAnyRole(
                                "SCHOOL_ADMIN", "PLATFORM_ADMIN"
                        )
                        .requestMatchers(HttpMethod.PUT, "/schools/update-logo").hasAnyRole(
                                "SCHOOL_ADMIN", "PLATFORM_ADMIN"
                        )
                        .requestMatchers(HttpMethod.GET, "/schools/school").hasAnyRole(
                                "USER", "STUDENT", "TEACHER", "SCHOOL_ADMIN", "PLATFORM_ADMIN"
                        )
                        .requestMatchers(HttpMethod.GET, "/schools/{id}/teachers").hasAnyRole(
                                "USER", "STUDENT", "TEACHER", "SCHOOL_ADMIN", "PLATFORM_ADMIN"
                        )
                        .requestMatchers(HttpMethod.POST, "schools/achievements/create").hasAnyRole(
                                "SCHOOL_ADMIN", "PLATFORM_ADMIN"
                        )
                        .requestMatchers(HttpMethod.GET, "schools/achievements").hasAnyRole(
                                "USER", "STUDENT", "TEACHER", "SCHOOL_ADMIN", "PLATFORM_ADMIN"
                        )
                        .requestMatchers(HttpMethod.PUT, "schools/achievements/{id}").hasAnyRole(
                                "SCHOOL_ADMIN", "PLATFORM_ADMIN"
                        )
                        .requestMatchers(HttpMethod.DELETE, "schools/achievements/{id}").hasAnyRole(
                                "SCHOOL_ADMIN", "PLATFORM_ADMIN"
                        )
                        .requestMatchers(HttpMethod.GET, "schools/all-gallery-image").hasAnyRole(
                                        "USER", "STUDENT", "TEACHER", "SCHOOL_ADMIN", "PLATFORM_ADMIN"
                        )
                        .requestMatchers(HttpMethod.POST, "schools/add-gallery-image").hasAnyRole(
                                "SCHOOL_ADMIN", "PLATFORM_ADMIN"
                        )
                        .requestMatchers(HttpMethod.DELETE, "schools/delete-gallery-image").hasAnyRole(
                                "SCHOOL_ADMIN", "PLATFORM_ADMIN"
                        )

                        // UserController
                        .requestMatchers(HttpMethod.GET, "/users/{id}").hasAnyRole(
                                "PLATFORM_ADMIN", "SCHOOL_ADMIN", "TEACHER", "STUDENT", "USER"
                        )
                        .requestMatchers(HttpMethod.GET, "/users").hasAnyRole(
                                "PLATFORM_ADMIN", "SCHOOL_ADMIN", "TEACHER", "STUDENT", "USER"
                        )
                        .requestMatchers(HttpMethod.PUT, "/users/{id}/info").hasAnyRole(
                                "PLATFORM_ADMIN", "SCHOOL_ADMIN", "TEACHER", "STUDENT", "USER"
                        )
                        .requestMatchers(HttpMethod.PUT, "/users/{id}/image").hasAnyRole(
                                "PLATFORM_ADMIN", "SCHOOL_ADMIN", "TEACHER", "STUDENT", "USER"
                        )
                        .requestMatchers(HttpMethod.PUT, "/users/{id}/duty").hasAnyRole(
                                "PLATFORM_ADMIN", "SCHOOL_ADMIN"
                        )
                        .requestMatchers(HttpMethod.DELETE, "/users/{id}").hasAnyRole(
                                "PLATFORM_ADMIN", "SCHOOL_ADMIN", "TEACHER", "STUDENT", "USER"
                        )
                        .requestMatchers(HttpMethod.GET, "/users/{id}/grades").hasAnyRole(
                                "PLATFORM_ADMIN", "SCHOOL_ADMIN", "TEACHER", "STUDENT", "USER"
                        )
                        .requestMatchers(HttpMethod.PUT, "/users/{id}/name").hasAnyRole(
                                "SCHOOL_ADMIN", "PLATFORM_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/users/profile").hasAnyRole(
                                "USER", "STUDENT", "TEACHER", "SCHOOL_ADMIN", "PLATFORM_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/users/roles").hasAnyRole(
                                "USER", "STUDENT", "TEACHER", "SCHOOL_ADMIN", "PLATFORM_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/users/school-id").hasAnyRole(
                                "USER", "STUDENT", "TEACHER", "SCHOOL_ADMIN", "PLATFORM_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/users/my-id").hasAnyRole(
                                "USER", "STUDENT", "TEACHER", "SCHOOL_ADMIN", "PLATFORM_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/users/profile/{id}").hasAnyRole(
                                "USER", "STUDENT", "TEACHER", "SCHOOL_ADMIN", "PLATFORM_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/users/{id}/update-roles").hasAnyRole(
                                "USER", "STUDENT", "TEACHER", "SCHOOL_ADMIN", "PLATFORM_ADMIN")
                        .requestMatchers("/users/**").hasAnyRole(
                                "USER", "STUDENT", "TEACHER", "SCHOOL_ADMIN", "PLATFORM_ADMIN")

                        // ModuleController
                        .requestMatchers(HttpMethod.POST, "/modules").hasAnyRole(
                                "PLATFORM_ADMIN", "SCHOOL_ADMIN", "TEACHER"
                        )
                        .requestMatchers(HttpMethod.GET, "/modules/{id}").hasAnyRole(
                                "PLATFORM_ADMIN", "SCHOOL_ADMIN", "TEACHER", "STUDENT"
                        )
                        .requestMatchers(HttpMethod.PUT, "/modules/{id}").hasAnyRole(
                                "PLATFORM_ADMIN", "SCHOOL_ADMIN", "TEACHER"
                        )
                        .requestMatchers(HttpMethod.DELETE, "/modules/{id}").hasAnyRole(
                                "PLATFORM_ADMIN", "SCHOOL_ADMIN", "TEACHER"
                        )
                        .requestMatchers(HttpMethod.GET, "/modules/{id}/materials").hasAnyRole(
                                "PLATFORM_ADMIN", "SCHOOL_ADMIN", "TEACHER", "STUDENT"
                        )
                        .requestMatchers(HttpMethod.GET, "/modules/{id}/assignments").hasAnyRole(
                                "PLATFORM_ADMIN", "SCHOOL_ADMIN", "TEACHER", "STUDENT"
                        )

                        // MaterialController
                        .requestMatchers(HttpMethod.POST, "/materials").hasAnyRole(
                                "PLATFORM_ADMIN", "SCHOOL_ADMIN", "TEACHER"
                        )
                        .requestMatchers(HttpMethod.GET, "/materials/{id}").hasAnyRole(
                                "PLATFORM_ADMIN", "SCHOOL_ADMIN", "TEACHER", "STUDENT"
                        )
                        .requestMatchers(HttpMethod.PUT, "/materials/{id}").hasAnyRole(
                                "PLATFORM_ADMIN", "SCHOOL_ADMIN", "TEACHER"
                        )
                        .requestMatchers(HttpMethod.DELETE, "/materials/{id}").hasAnyRole(
                                "PLATFORM_ADMIN", "SCHOOL_ADMIN", "TEACHER"
                        )

                        // AssignmentController
                        .requestMatchers(HttpMethod.POST, "/assignments").hasAnyRole(
                                "PLATFORM_ADMIN", "SCHOOL_ADMIN", "TEACHER"
                        )
                        .requestMatchers(HttpMethod.GET, "/assignments/{id}").hasAnyRole(
                                "PLATFORM_ADMIN", "SCHOOL_ADMIN", "TEACHER", "STUDENT"
                        )
                        .requestMatchers(HttpMethod.PUT, "/assignments/{id}").hasAnyRole(
                                "PLATFORM_ADMIN", "SCHOOL_ADMIN", "TEACHER"
                        )
                        .requestMatchers(HttpMethod.DELETE, "/assignments/{id}").hasAnyRole(
                                "PLATFORM_ADMIN", "SCHOOL_ADMIN", "TEACHER"
                        )
                        .requestMatchers(HttpMethod.GET, "/assignments/{id}/submissions").hasAnyRole(
                                "PLATFORM_ADMIN", "SCHOOL_ADMIN", "TEACHER", "USER"
                        )

                        // SubmissionController
                        .requestMatchers(HttpMethod.POST, "/submissions").hasRole("STUDENT")
                        .requestMatchers(HttpMethod.GET, "/submissions/{id}").hasAnyRole(
                                "PLATFORM_ADMIN", "SCHOOL_ADMIN", "TEACHER", "STUDENT"
                        )
                        .requestMatchers(HttpMethod.PUT, "/submissions/{id}").hasRole("STUDENT")
                        .requestMatchers(HttpMethod.PUT, "/submissions/{id}/grade").hasRole("TEACHER")
                        .requestMatchers(HttpMethod.DELETE, "/submissions/{id}").hasAnyRole(
                                "TEACHER", "STUDENT"
                        )

                        // ScheduleController
                        .requestMatchers(HttpMethod.POST, "/schedules/").hasRole("SCHOOL_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/schedules/{id}").hasAnyRole(
                                "STUDENT", "TEACHER", "SCHOOL_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/schedules/").hasAnyRole(
                                "STUDENT", "TEACHER", "SCHOOL_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/schedules/class/{id}").hasAnyRole(
                                "STUDENT", "TEACHER", "SCHOOL_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/schedules/{id}").hasRole("SCHOOL_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/schedules/{id}").hasRole("SCHOOL_ADMIN")
                        .requestMatchers("/schedules/**").hasAnyRole(
                                "STUDENT", "TEACHER", "SCHOOL_ADMIN")

                        // NewsController
                        .requestMatchers("/news/new").hasRole("SCHOOL_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/news/{id}").hasRole("SCHOOL_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/news/{id}").hasRole("SCHOOL_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/news/{id}").hasRole("SCHOOL_ADMIN")
                        .requestMatchers("/news/**").hasAnyRole(
                                "STUDENT", "TEACHER", "SCHOOL_ADMIN", "PLATFORM_ADMIN")

                        // RolesController
                        .requestMatchers("/roles/**").hasAnyRole("SCHOOL_ADMIN", "PLATFORM_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/roles/").hasAnyRole("SCHOOL_ADMIN", "PLATFORM_ADMIN")
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
