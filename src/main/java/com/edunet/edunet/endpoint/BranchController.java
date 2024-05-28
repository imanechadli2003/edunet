package com.edunet.edunet.endpoint;


import com.edunet.edunet.model.Branch;
import com.edunet.edunet.service.BranchService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/branches", produces = "application/json")
@AllArgsConstructor
@CrossOrigin(methods = {RequestMethod.OPTIONS, RequestMethod.DELETE, RequestMethod.GET, RequestMethod.HEAD, RequestMethod.PUT, RequestMethod.POST}, origins = "*")
public class BranchController {

    private final BranchService branchService;

    @GetMapping
    public List<Branch> getAllBranches() {
        return this.branchService.all();
    }

    @GetMapping("/{id}")
    public Branch getBranch(@PathVariable int id) {
        return this.branchService.getBranch(id);
    }

}
