package com.edunet.edunet.endpoint;


import com.edunet.edunet.dto.AuthToken;
import com.edunet.edunet.dto.Login;
import com.edunet.edunet.security.AuthenticationService;
import com.edunet.edunet.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/auth", produces = "application/json")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class AuthenticationController {

    private final AuthenticationService authService;

    private final UserService userService;

    @PostMapping("/token")
    public AuthToken getToken(@RequestBody Login credentials) {
        return authService.getToken(credentials);
    }

    @GetMapping("/user")
    public AuthToken getAuthenticatedUserData() {
        return userService.getAuthenticatedUser();
    }
}
