package com.edunet.edunet.security;

import com.edunet.edunet.repository.UserRepository;
import com.edunet.edunet.repository.projections.Credentials;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class AuthenticationManagerImpl implements AuthenticationManager {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        String handle = auth.getName();
        Credentials creds = userRepository.findByHandle(handle)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        String password = creds.getPassword();
        String providedPassword = (String) auth.getCredentials();
        if (!passwordEncoder.matches(providedPassword, password)) {
            throw new BadCredentialsException("Wrong password");
        }
        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority(creds.getRole().getName())
        );
        AuthenticatedUserDetails details = new AuthenticatedUserDetails(creds.getId());
        return new AuthenticationImpl(handle, null, authorities, details);
    }
}
