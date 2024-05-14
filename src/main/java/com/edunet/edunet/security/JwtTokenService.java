package com.edunet.edunet.security;


import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@AllArgsConstructor
public class JwtTokenService {

    private final JwtEncoder encoder;

    public String generateToken(Authentication auth) {
        AuthenticationImpl authentication = (AuthenticationImpl) auth;
        Instant now = Instant.now();
        String scope = auth.getAuthorities().stream().limit(1).toList().getFirst().getAuthority();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .subject(auth.getName())
                .claim("userId", authentication.details().id())
                .claim("scope", scope)
                .issuedAt(now)
                .build();
        return this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
