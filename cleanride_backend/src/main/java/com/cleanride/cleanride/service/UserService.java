package com.cleanride.cleanride.service;

import com.cleanride.cleanride.dto.request.CreateUserRequest;
import com.cleanride.cleanride.dto.request.LoginRequest;
import com.cleanride.cleanride.dto.request.UpdateUserRequest;
import com.cleanride.cleanride.dto.response.UserResponse;
import com.cleanride.cleanride.entity.Users;
import com.cleanride.cleanride.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UsersRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ez az email cím már foglalt: " + request.getEmail());
        }
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ez a telefonszám már foglalt: " + request.getPhone());
        }

        Users saved = userRepository.createUser(
                request.getName(),
                request.getEmail(),
                request.getPhone(),
                passwordEncoder.encode(request.getPassword()));

        return toResponseDto(saved);
    }

    public List<UserResponse> getAll() {
        return userRepository.getAllUsers().stream()
                .map(this::toResponseDto)
                .toList();
    }

    public UserResponse getById(Integer id) {
        return toResponseDto(findActiveUser(id));
    }

    public UserResponse login(LoginRequest request) {
        Users user = userRepository.getUserByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Hibás email vagy jelszó"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Hibás email vagy jelszó");
        }

        return toResponseDto(user);
    }

    @Transactional
    public UserResponse updateUser(Integer id, UpdateUserRequest request) {
        Users user = findActiveUser(id);

        if (!user.getEmail().equals(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ez az email cím már foglalt: " + request.getEmail());
        }
        if (!user.getPhone().equals(request.getPhone())
                && userRepository.existsByPhone(request.getPhone())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ez a telefonszám már foglalt: " + request.getPhone());
        }

        userRepository.updateUser(id, request.getName(), request.getEmail(), request.getPhone());
        return getById(id);
    }

    @Transactional
    public void softDelete(Integer id) {
        findActiveUser(id);
        userRepository.deleteUser(id);
    }

    private Users findActiveUser(Integer id) {
        return userRepository.getUserById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Nincs ilyen felhasználó: " + id));
    }

    private UserResponse toResponseDto(Users user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .loyaltyLevel(user.getLoyaltyLevel())
                .build();
    }
}