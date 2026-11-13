package com.cleanride.cleanride.config;

import com.cleanride.cleanride.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthFilter jwtAuthFilter;

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(csrf -> csrf.disable())
                    .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .exceptionHandling(e -> e.authenticationEntryPoint(
                            new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers(HttpMethod.POST, "/api/users/login").permitAll()
                            .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                            .requestMatchers(HttpMethod.POST, "/api/admins/login").permitAll()
                            .requestMatchers(HttpMethod.GET, "/api/services/**").permitAll()
                            .requestMatchers("/api/admins/**").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.POST, "/api/services").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.PUT, "/api/services/**").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.DELETE, "/api/services/**").hasRole("ADMIN")
                            .requestMatchers("/api/users/**").hasRole("ADMIN")
                            .requestMatchers("/api/me/**").authenticated()
                            .anyRequest().authenticated()
                    )

                    .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

            return http.build();
        }
    }