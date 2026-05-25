package com.example.rackapp.filter;

import java.io.IOException;

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

@Component("appCorsFilter")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest httpRequest && response instanceof HttpServletResponse httpResponse) {
            String origin = httpRequest.getHeader("Origin");
            if (origin != null && !origin.isBlank()) {
                httpResponse.setHeader("Access-Control-Allow-Origin", origin);
                httpResponse.setHeader("Vary", "Origin");
            }
            httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            httpResponse.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, X-Requested-With, X-Api-Key");
            httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
            httpResponse.setHeader("Access-Control-Max-Age", "3600");

            if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
                httpResponse.setStatus(HttpServletResponse.SC_OK);
                return;
            }
        }

        chain.doFilter(request, response);
    }
}
