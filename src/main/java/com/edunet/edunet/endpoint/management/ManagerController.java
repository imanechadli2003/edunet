package com.edunet.edunet.endpoint.management;

import com.edunet.edunet.security.ManagementService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/manager", produces = "application/json")
@AllArgsConstructor
public class ManagerController {

    private final ManagementService managementService;

    @DeleteMapping("/user/{id}")
    public void deleteUser(@PathVariable int id) {
        managementService.deleteUserAccount(id);
    }

    @DeleteMapping("/topic/{id}")
    public void deleteTopic(@PathVariable int id) {
        managementService.deleteTopic(id);
    }
}
