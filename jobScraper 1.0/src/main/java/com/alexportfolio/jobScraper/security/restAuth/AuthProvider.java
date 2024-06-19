package com.alexportfolio.jobScraper.security.restAuth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class AuthProvider implements AuthenticationProvider {
    @Value("${rest.Key}")
    String myKey;
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        AuthenticationObj auth = (AuthenticationObj) authentication;

        if(auth.getApiKey()!=null && auth.getApiKey().equals(myKey))
            return new AuthenticationObj(null, true);
        throw new AuthenticationException("Invalid key"){};
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return AuthenticationObj.class.equals(authentication);
    }
}
