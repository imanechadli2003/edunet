package com.edunet.edunet.service;


import com.edunet.edunet.exception.BadRequestException;
import com.edunet.edunet.exception.ResourceNotFoundException;
import com.edunet.edunet.model.Branch;
import com.edunet.edunet.repository.BranchRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BranchService {

    private final BranchRepository branchRepository;

    public List<Branch> all() {
        return branchRepository.findAll();
    }

    public void delete(int id) {
        this.branchRepository.deleteById(id);
    }

    public Branch addBranch(Branch branch) {
        try {
            return this.branchRepository.save(branch);
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("");
        }
    }

    public Branch update(int id, Branch branch) {
        Branch updated = new Branch();
        updated.setId(id);
        if (branch.getName() == null || branch.getName().isEmpty()) {
            throw new BadRequestException("");
        }
        try {
            updated.setName(branch.getName());
        } catch (DataIntegrityViolationException ignored) {
            throw new BadRequestException("");
        }
        updated.setDescription(branch.getDescription());
        this.branchRepository.save(updated);
        return updated;
    }

    public Branch getBranch(int id) {
        return this.branchRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("branch " + id)
        );
    }
}
