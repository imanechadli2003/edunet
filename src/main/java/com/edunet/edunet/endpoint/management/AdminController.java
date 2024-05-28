package com.edunet.edunet.endpoint.management;


import com.edunet.edunet.dto.RoleDto;
import com.edunet.edunet.security.ManagementService;
import com.edunet.edunet.service.TopicService;
import com.edunet.edunet.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/admin", produces = "application/json")
@AllArgsConstructor
@CrossOrigin(methods = {RequestMethod.OPTIONS, RequestMethod.DELETE, RequestMethod.GET, RequestMethod.HEAD, RequestMethod.PUT, RequestMethod.POST}, origins = "*")
public class AdminController {

    private final ManagementService managementService;

    private UserService userService;

    private TopicService topicService;

    @PostMapping("/users/{id}/update-role")
    public void updateUserRole(@PathVariable long id, @RequestBody RoleDto role) {
        managementService.updateUserRole(id, role);
    }

    @DeleteMapping("/users/{id}")
    public void adminDeleteUser(@PathVariable long id) {
        this.userService.adminDeleteUser(id);
    }

    @DeleteMapping("/topics/{id}")
    public void delete(@PathVariable int id) {
        this.topicService.adminDeleteTopic(id);
    }
}
