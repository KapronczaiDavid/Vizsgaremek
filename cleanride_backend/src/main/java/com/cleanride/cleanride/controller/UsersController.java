package com.cleanride.cleanride.controller;
import com.cleanride.cleanride.entity.Users;
import com.cleanride.cleanride.repository.UsersRepository;
import org.springframework.cglib.core.Local;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController @RequestMapping("/api/users")
public class UsersController {
    private final UsersRepository usersRepository;

    public UsersController(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }
    @GetMapping
    public List<Users> getAll() {
        return usersRepository.findAll();
    }
    @PostMapping
    public Users create(@RequestParam String name, @RequestParam String email, @RequestParam String phone,
                        @RequestParam String password
                        ) {

        Users user = new Users();
        user.setName(name);
        user.setEmail(email);
        user.setPhone(phone);
        user.setPassword(password);


        return usersRepository.save(user);
    }
    }
