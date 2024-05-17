package com.edunet.edunet.endpoint;


import com.edunet.edunet.dto.Login;
import com.edunet.edunet.security.AuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/auth", produces = "application/json")
@AllArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authService;

    @PostMapping("/token")
    public String getToken(@RequestBody Login credentials) {
        return authService.getToken(credentials);
    }
}