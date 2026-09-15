package com.cleanride.cleanride.controller;
import com.cleanride.cleanride.entity.Ping;
import com.cleanride.cleanride.repository.PingRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/ping")
public class PingController {
    private final PingRepository pingRepository;

    public PingController(PingRepository pingRepository) {
        this.pingRepository = pingRepository;
    }

    @GetMapping
    public List<Ping> getAll() {
        return pingRepository.findAll();
    }

    @PostMapping
    public Ping create(@RequestParam String message) {
        Ping ping = new Ping();
        ping.setMessage(message);
        return pingRepository.save(ping);
    }
}