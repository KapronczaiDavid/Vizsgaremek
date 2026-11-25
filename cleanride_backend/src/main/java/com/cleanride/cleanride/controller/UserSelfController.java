package com.cleanride.cleanride.controller;



import com.cleanride.cleanride.dto.request.UpdateUserRequest;
import com.cleanride.cleanride.dto.response.UserResponse;
import com.cleanride.cleanride.security.AuthPrincipal;
import com.cleanride.cleanride.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

    @RestController
    @RequestMapping("/api/me")
    @RequiredArgsConstructor
public class UserSelfController {


        private final UserService userService;

        @GetMapping
        public UserResponse getMe(@AuthenticationPrincipal AuthPrincipal principal) {
            return userService.getById(principal.id());
        }

        @PutMapping
        public UserResponse updateMe(@AuthenticationPrincipal AuthPrincipal principal,
                                     @Valid @RequestBody UpdateUserRequest request) {
            return userService.updateUser(principal.id(), request);
        }
    }
