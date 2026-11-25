package com.cleanride.cleanride.controller;

import com.cleanride.cleanride.dto.request.LoginRequest;
import com.cleanride.cleanride.dto.response.AdminResponse;
import com.cleanride.cleanride.service.AdminService;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @PostMapping("/login")
    public AdminResponse login(@Valid @RequestBody LoginRequest request, HttpServletRequest http) {
        return adminService.login(request, http.getRemoteAddr());
    }

}
