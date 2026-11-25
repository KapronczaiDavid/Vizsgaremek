package com.cleanride.cleanride.controller;

import com.cleanride.cleanride.dto.request.CreateUserRequest;
import com.cleanride.cleanride.dto.request.LoginRequest;
import com.cleanride.cleanride.dto.request.ResendVerificationRequest;
import com.cleanride.cleanride.dto.request.VerifyEmailRequest;
import com.cleanride.cleanride.service.EmailVerificationService;
import com.cleanride.cleanride.dto.request.UpdateUserRequest;
import com.cleanride.cleanride.dto.response.UserResponse;
import com.cleanride.cleanride.service.UserService;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.cleanride.cleanride.util.RequestRateLimiter;
import java.time.Duration;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UsersController {

    private final UserService userService;
    private final EmailVerificationService emailVerificationService;
    private final RequestRateLimiter rateLimiter;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request,
                                                   HttpServletRequest http) {
        rateLimiter.check("register|" + http.getRemoteAddr(), 10, Duration.ofHours(1));
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
    }
    @PostMapping("/login")
    public UserResponse login(@Valid @RequestBody LoginRequest request, HttpServletRequest http) {
        return userService.login(request, http.getRemoteAddr());
    }

    @PostMapping("/verify-email")
    public ResponseEntity<Void> verifyEmail(@Valid @RequestBody VerifyEmailRequest request,
                                            HttpServletRequest http) {
        rateLimiter.check("verify|" + http.getRemoteAddr(), 20, Duration.ofMinutes(15));
        emailVerificationService.verify(request.getEmail(), request.getCode());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<Void> resendVerification(@Valid @RequestBody ResendVerificationRequest request,
                                                   HttpServletRequest http) {
        rateLimiter.check("resend|" + http.getRemoteAddr(), 10, Duration.ofHours(1));
        emailVerificationService.resend(request.getEmail());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public List<UserResponse> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable Integer id) {
        return userService.getById(id);
    }

    @PutMapping("/{id}")
    public UserResponse updateUser(@PathVariable Integer id,
                                   @Valid @RequestBody UpdateUserRequest request) {
        return userService.updateUser(id, request);
    }
    @DeleteMapping("/{id}")
    public void softDelete(@PathVariable Integer id) {
        userService.softDelete(id);
    }
}