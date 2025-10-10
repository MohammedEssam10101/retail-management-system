package com.retail.management.service;

import com.retail.management.dto.request.branch.CreateBranchRequest;
import com.retail.management.dto.request.branch.UpdateBranchRequest;
import com.retail.management.dto.response.branch.BranchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BranchService {
    BranchResponse createBranch(CreateBranchRequest request);
    BranchResponse getBranchById(Long id);
    Page<BranchResponse> getAllBranches(Pageable pageable);
    List<BranchResponse> getActiveBranches();
    BranchResponse updateBranch(Long id, UpdateBranchRequest request);
    void deleteBranch(Long id);
    void deactivateBranch(Long id);
}