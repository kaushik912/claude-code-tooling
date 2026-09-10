package com.kaushik.userregistration.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple in-memory fixed-window limiter for the public registration endpoint,
 * keyed by client IP. Good enough for a single-instance demo; a multi-instance
 * deployment would need a shared store (e.g. Redis) instead.
 */
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final String LIMITED_PATH = "/api/users/register";
    private static final int MAX_REQUESTS_PER_WINDOW = 5;
    private static final long WINDOW_MILLIS = 60_000L;

    private final ConcurrentHashMap<String, Window> windowsByIp = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (!LIMITED_PATH.equals(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = request.getRemoteAddr();
        Window window = windowsByIp.computeIfAbsent(clientIp, ip -> new Window());

        if (window.isExceeded()) {
            response.setStatus(429); // 429 Too Many Requests
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"too many registration attempts, try again later\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private static final class Window {
        private volatile long windowStartMillis = Instant.now().toEpochMilli();
        private final AtomicInteger count = new AtomicInteger(0);

        synchronized boolean isExceeded() {
            long now = Instant.now().toEpochMilli();
            if (now - windowStartMillis > WINDOW_MILLIS) {
                windowStartMillis = now;
                count.set(0);
            }
            return count.incrementAndGet() > MAX_REQUESTS_PER_WINDOW;
        }
    }
}
