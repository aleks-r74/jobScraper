package com.alexportfolio.jobScraper.security.restAuth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;
import java.util.List;

public class AuthenticationObj implements Authentication {
    private boolean authenticated;
    private String apiKey;

    public AuthenticationObj(String apiKey,boolean authenticated) {
        this.authenticated = authenticated;
        this.apiKey = apiKey;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
       authenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return "";
    }
}
