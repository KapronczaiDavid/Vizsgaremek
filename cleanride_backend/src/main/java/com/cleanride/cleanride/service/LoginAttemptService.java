package com.cleanride.cleanride.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple in-memory brute-force protection for login endpoints.
 * Single-instance only; use a shared store (Redis, DB) if you run several instances.
 */
@Service
public class LoginAttemptService {

    private static final int MAX_FAILURES = 5;
    private static final Duration WINDOW = Duration.ofMinutes(15);
    private static final int CLEANUP_THRESHOLD = 10_000;

    private record Entry(int failures, Instant windowStart) {}

    private final ConcurrentHashMap<String, Entry> entries = new ConcurrentHashMap<>();

    public void checkAllowed(String key) {
        Entry e = entries.get(key);
        if (e != null && !isExpired(e) && e.failures() >= MAX_FAILURES) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "Túl sok sikertelen bejelentkezési kísérlet, próbáld újra később");
        }
    }

    public void recordFailure(String key) {
        Instant now = Instant.now();
        entries.merge(key, new Entry(1, now), (old, ignored) ->
                isExpired(old) ? new Entry(1, now) : new Entry(old.failures() + 1, old.windowStart()));
        if (entries.size() > CLEANUP_THRESHOLD) {
            entries.entrySet().removeIf(en -> isExpired(en.getValue()));
        }
    }

    public void reset(String key) {
        entries.remove(key);
    }

    private boolean isExpired(Entry e) {
        return e.windowStart().plus(WINDOW).isBefore(Instant.now());
    }
}
