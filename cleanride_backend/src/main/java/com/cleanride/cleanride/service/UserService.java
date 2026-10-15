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

import java.time.LocalDateTime;
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

        Users user = Users.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .build();

        Users saved = userRepository.save(user);
        return toResponseDto(saved);
    }

    public List<UserResponse> getAll() {
        return userRepository.findAllActive().stream()
                .map(this::toResponseDto)
                .toList();
    }

    public UserResponse getById(Integer id) {
        return toResponseDto(findActiveUser(id));
    }

    public UserResponse login(LoginRequest request) {
        Users user = userRepository.findActiveByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Hibás email vagy jelszó"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Hibás email vagy jelszó");
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

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());

        Users saved = userRepository.save(user);
        return toResponseDto(saved);
    }

    @Transactional
    public void softDelete(Integer id) {
        Users user = findActiveUser(id);

        user.setIsDeleted(true);
        user.setDeletedAt(LocalDateTime.now());

        userRepository.save(user);
    }

    private Users findActiveUser(Integer id) {
        return userRepository.findActiveById(id)
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