package com.example.rackapp.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RequestTimingInterceptor implements HandlerInterceptor {

    private static final String START_TIME_ATTRIBUTE = "requestStartTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        long startTime = System.currentTimeMillis();
        request.setAttribute(START_TIME_ATTRIBUTE, startTime);
        System.out.println("HandlerInterceptor preHandle: " + request.getMethod() + " " + request.getRequestURI());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        Object startTimeObj = request.getAttribute(START_TIME_ATTRIBUTE);
        if (startTimeObj instanceof Long startTime) {
            long duration = System.currentTimeMillis() - startTime;
            System.out.println("HandlerInterceptor afterCompletion: " + request.getMethod() + " " + request.getRequestURI() + " completed in " + duration + " ms");
        }
    }
}
