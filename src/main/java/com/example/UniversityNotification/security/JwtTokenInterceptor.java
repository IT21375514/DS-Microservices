package com.example.UniversityNotification.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtTokenInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Extract JWT token from request header
        String jwtToken = request.getHeader("Authorization");

        if (jwtToken == null || !jwtUtils.validateJwtToken(jwtToken)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or missing JWT token");
            return false; // Stops the request processing
        }
//        if (jwtToken == null || !jwtUtils.validateJwtToken(jwtToken)) {
////            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or missing JWT token");
////            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body("Invalid or missing JWT token");
////        }

        // Proceed with the request
        return true;
    }
}
