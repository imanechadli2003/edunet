package com.edunet.edunet.endpoint.management;


import com.edunet.edunet.dto.RoleDto;
import com.edunet.edunet.security.ManagementService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/admin", produces = "application/json")
@AllArgsConstructor
public class AdminController {

    private final ManagementService managementService;

    @PostMapping("/users/{id}/update-role")
    public void updateUserRole(@PathVariable long id, @RequestBody RoleDto role) {
        managementService.updateUserRole(id, role);
    }

}
