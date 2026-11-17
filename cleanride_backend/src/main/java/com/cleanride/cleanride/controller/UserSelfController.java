package com.cleanride.cleanride.controller;



import com.cleanride.cleanride.dto.request.UpdateUserRequest;
import com.cleanride.cleanride.dto.response.UserResponse;
import com.cleanride.cleanride.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

    @RestController
    @RequestMapping("/api/me")
    @RequiredArgsConstructor
public class UserSelfController {


        private final UserService userService;

        @GetMapping
        public UserResponse getMe(Authentication authentication) {
            Integer userId = (Integer) authentication.getPrincipal();
            return userService.getById(userId);
        }

        @PutMapping
        public UserResponse updateMe(Authentication authentication,
                                     @Valid @RequestBody UpdateUserRequest request) {
            Integer userId = (Integer) authentication.getPrincipal();
            return userService.updateUser(userId, request);
        }
    }
