package com.edunet.edunet.security;


import com.edunet.edunet.dto.Login;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;

    private final JwtTokenService jwtTokenService;

    public String getToken(Login credentials) {
        var authToken = new UsernamePasswordAuthenticationToken(credentials.handle(), credentials.password());
        Authentication auth = authenticationManager.authenticate(authToken);
        return jwtTokenService.generateToken(auth);
    }
}
