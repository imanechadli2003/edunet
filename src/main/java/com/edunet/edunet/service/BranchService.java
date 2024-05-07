package com.edunet.edunet.service;


import com.edunet.edunet.models.Branch;
import com.edunet.edunet.repository.BranchRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class BranchService {

    private final BranchRepository branchRepository;

    public Optional<Branch> getBranch(String name) {
        return branchRepository.findBranchByName(name);
    }
}
