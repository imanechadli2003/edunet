package com.edunet.edunet.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class AuthenticationImpl extends UsernamePasswordAuthenticationToken {

    private final AuthenticatedUserDetails details;

    public AuthenticationImpl(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities, AuthenticatedUserDetails details) {
        super(principal, credentials, authorities);
        this.details = details;
    }

    public AuthenticatedUserDetails details() {
        return details;
    }
}
