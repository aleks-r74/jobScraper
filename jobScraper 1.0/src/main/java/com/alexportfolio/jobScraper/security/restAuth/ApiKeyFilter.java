package com.alexportfolio.jobScraper.security.restAuth;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ApiKeyFilter extends OncePerRequestFilter  {
    ApiKeyManager apiKeyManager;

    public ApiKeyFilter(ApiKeyManager apiKeyManager) {
        this.apiKeyManager = apiKeyManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String key = request.getHeader("Key");
        AuthenticationObj authObj = new AuthenticationObj(key, false);
        try{
            //var apiKeyManager = new ApiKeyManager(new AuthProvider());
            var  authResultObj = apiKeyManager.authenticate(authObj);
            if(authResultObj.isAuthenticated()){
                SecurityContextHolder.getContext().setAuthentication(authResultObj);
                filterChain.doFilter(request, response);
            }
        } catch(AuthenticationException e){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
