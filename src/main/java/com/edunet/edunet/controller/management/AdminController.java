package com.edunet.edunet.controller.management;


import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/admin", produces = "application/json")
@AllArgsConstructor
public class AdminController {


    @DeleteMapping("/delete/user")
    public void deleteUser() {
        // TODO
    }



    @GetMapping("/")
    public String welcome() {
        return "Hello Admin";
    }

}
