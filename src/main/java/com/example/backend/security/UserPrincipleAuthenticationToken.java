package com.example.backend.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class UserPrincipleAuthenticationToken extends AbstractAuthenticationToken {

    private final UserPrinciple principle;

    public UserPrincipleAuthenticationToken(UserPrinciple principle) {
        super(principle.getAuthorities());
        this.principle=principle;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public UserPrinciple getPrincipal() {
        return principle ;
    }
}
