package com.example.rackapp.filter;

import java.io.IOException;

import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class RequestLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest httpRequest) {
            String method = httpRequest.getMethod();
            String uri = httpRequest.getRequestURI();
            String remoteAddr = request.getRemoteAddr();
            System.out.println("Servlet Filter: " + method + " " + uri + " from " + remoteAddr);
        }

        chain.doFilter(request, response);
    }
}
