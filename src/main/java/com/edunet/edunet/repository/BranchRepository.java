package com.edunet.edunet.repository;

import com.edunet.edunet.model.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BranchRepository extends JpaRepository<Branch, Integer> {

    Optional<Branch> findBranchByName(String name);
}
