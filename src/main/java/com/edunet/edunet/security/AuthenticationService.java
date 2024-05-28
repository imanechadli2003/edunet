package com.edunet.edunet.security;


import com.edunet.edunet.dto.AuthToken;
import com.edunet.edunet.dto.Login;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;

    private final JwtTokenService jwtTokenService;

    public AuthToken getToken(Login credentials) {
        var authToken = new UsernamePasswordAuthenticationToken(credentials.handle(), credentials.password());
        Authentication auth = authenticationManager.authenticate(authToken);
        String jwtToken = jwtTokenService.generateToken(auth);
        return new AuthToken(
                ((AuthenticationImpl) auth).details().id(),
                auth.getName(),
                auth.getAuthorities().stream().toList().get(0).getAuthority(),
                jwtToken
        );
    }

    public long getAuthenticatedUserId() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return jwt.getClaim("userId");
    }

    public String getAuthenticatedUserHandle() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
