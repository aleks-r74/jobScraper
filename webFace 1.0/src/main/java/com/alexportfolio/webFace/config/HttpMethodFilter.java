package com.alexportfolio.webFace.config;

import jakarta.persistence.Access;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.naming.AuthenticationException;
import java.io.IOException;


public class HttpMethodFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String httpMethod = request.getMethod();
        if(!isValidHttpMethod(httpMethod)) {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Invalid HTTP method");
            System.out.println("Got Invalid HTTP Method: " + httpMethod);
            return;
        }
        filterChain.doFilter(request,response);
    }

    private boolean isValidHttpMethod(String method) {
        // Check against standard HTTP methods
        return "GET".equalsIgnoreCase(method) || "POST".equalsIgnoreCase(method) ||
                "PUT".equalsIgnoreCase(method) || "DELETE".equalsIgnoreCase(method) ||
                "PATCH".equalsIgnoreCase(method) || "HEAD".equalsIgnoreCase(method) ||
                "OPTIONS".equalsIgnoreCase(method) || "TRACE".equalsIgnoreCase(method);
    }
}
