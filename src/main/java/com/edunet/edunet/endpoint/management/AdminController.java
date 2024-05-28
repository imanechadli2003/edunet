package com.edunet.edunet.endpoint.management;


import com.edunet.edunet.dto.RoleDto;
import com.edunet.edunet.model.Branch;
import com.edunet.edunet.security.ManagementService;
import com.edunet.edunet.service.BranchService;
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

    private BranchService branchService;

    @PostMapping("/users/{id}/update-role")
    public void updateUserRole(@PathVariable long id, @RequestBody RoleDto role) {
        managementService.updateUserRole(id, role);
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable long id) {
        this.userService.adminDeleteUser(id);
    }

    @DeleteMapping("/topics/{id}")
    public void deleteTopic(@PathVariable int id) {
        this.topicService.adminDeleteTopic(id);
    }

    @DeleteMapping("/branches/{id}")
    public void deleteBranch(@PathVariable int id) {
        this.branchService.delete(id);
    }

    @PostMapping("/branches")
    public Branch addBranch(@RequestBody Branch branch) {
        return this.branchService.addBranch(branch);
    }
    
    @PutMapping("/branches/{id}")
    public Branch updateBranch(@PathVariable int id, @RequestBody Branch branch) {
        return this.branchService.update(id, branch);
    }

}
