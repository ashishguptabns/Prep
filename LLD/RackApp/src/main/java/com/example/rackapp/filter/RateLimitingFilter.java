package com.example.rackapp.filter;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class RateLimitingFilter implements Filter {

    private static final int MAX_REQUESTS_PER_WINDOW = 50;
    private static final long WINDOW_MILLIS = 60_000L;

    private final ConcurrentMap<String, RequestWindow> requestWindows = new ConcurrentHashMap<>();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest httpRequest && response instanceof HttpServletResponse httpResponse) {
            String clientKey = getClientKey(httpRequest);
            long now = Instant.now().toEpochMilli();

            RequestWindow window = requestWindows.compute(clientKey, (key, existing) -> {
                if (existing == null || now - existing.windowStartMs >= WINDOW_MILLIS) {
                    return new RequestWindow(now, 1);
                }
                return new RequestWindow(existing.windowStartMs, existing.requestCount + 1);
            });

            if (window.requestCount > MAX_REQUESTS_PER_WINDOW) {
                long retryAfterSeconds = ((window.windowStartMs + WINDOW_MILLIS) - now + 999) / 1000;
                httpResponse.setStatus(429);
                httpResponse.setHeader("Retry-After", String.valueOf(Math.max(retryAfterSeconds, 1)));
                httpResponse.setHeader("X-RateLimit-Limit", String.valueOf(MAX_REQUESTS_PER_WINDOW));
                httpResponse.setHeader("X-RateLimit-Remaining", "0");
                httpResponse.getWriter().write("Too many requests");
                return;
            }

            int remaining = Math.max(MAX_REQUESTS_PER_WINDOW - window.requestCount, 0);
            httpResponse.setHeader("X-RateLimit-Limit", String.valueOf(MAX_REQUESTS_PER_WINDOW));
            httpResponse.setHeader("X-RateLimit-Remaining", String.valueOf(remaining));
        }

        chain.doFilter(request, response);
    }

    private String getClientKey(HttpServletRequest request) {
        String forwardedFor = Optional.ofNullable(request.getHeader("X-Forwarded-For")).orElse("");
        String remoteAddr = request.getRemoteAddr();
        String method = request.getMethod();
        String uri = request.getRequestURI();

        return String.format("%s|%s|%s|%s", forwardedFor.isBlank() ? remoteAddr : forwardedFor, method, uri, request.getHeader("Host"));
    }

    private static final class RequestWindow {
        final long windowStartMs;
        final int requestCount;

        RequestWindow(long windowStartMs, int requestCount) {
            this.windowStartMs = windowStartMs;
            this.requestCount = requestCount;
        }
    }
}
