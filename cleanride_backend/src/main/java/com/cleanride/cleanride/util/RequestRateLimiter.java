package com.cleanride.cleanride.util;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory fixed-window request counter (single instance only). */
@Service
public class RequestRateLimiter {

    private static final int CLEANUP_THRESHOLD = 10_000;
    private static final Duration MAX_AGE = Duration.ofHours(24);

    private record Window(int count, Instant start) {}

    private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();

    public void check(String key, int maxRequests, Duration window) {
        Instant now = Instant.now();
        Window w = windows.merge(key, new Window(1, now), (old, ignored) ->
                old.start().plus(window).isBefore(now)
                        ? new Window(1, now)
                        : new Window(old.count() + 1, old.start()));

        if (w.count() > maxRequests) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "Túl sok kérés, próbáld újra később");
        }
        if (windows.size() > CLEANUP_THRESHOLD) {
            windows.entrySet().removeIf(e -> e.getValue().start().plus(MAX_AGE).isBefore(now));
        }
    }
}
