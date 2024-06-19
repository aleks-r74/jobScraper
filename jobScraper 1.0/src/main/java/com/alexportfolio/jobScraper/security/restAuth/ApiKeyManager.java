package com.alexportfolio.jobScraper.security.restAuth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
@Component
public class ApiKeyManager implements AuthenticationManager {
    AuthProvider authProvider;

    public ApiKeyManager(AuthProvider authProvider) {
        this.authProvider = authProvider;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if(authProvider.supports(authentication.getClass()))
            return authProvider.authenticate(authentication);
        throw new AuthenticationException("Invalid API key"){};
    }
}
