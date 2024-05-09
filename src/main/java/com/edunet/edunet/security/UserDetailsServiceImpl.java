package com.edunet.edunet.security;

import com.edunet.edunet.model.Role;
import com.edunet.edunet.model.User;
import com.edunet.edunet.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByHandle(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        String password = user.getPassword();
        Role role = user.getRole();
        List<GrantedAuthority> roles = List.of(new SimpleGrantedAuthority(role.getName()));
        return new org.springframework.security.core.userdetails.User(username, password, roles);
    }
}
