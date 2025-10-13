package com.retail.management.service.impl;

import com.retail.management.dto.request.branch.CreateBranchRequest;
import com.retail.management.dto.request.branch.UpdateBranchRequest;
import com.retail.management.dto.response.branch.BranchResponse;
import com.retail.management.entity.Branch;
import com.retail.management.exception.DuplicateResourceException;
import com.retail.management.exception.ResourceNotFoundException;
import com.retail.management.mapper.BranchMapper;
import com.retail.management.repository.BranchRepository;
import com.retail.management.service.BranchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;
    private final BranchMapper branchMapper;

    @Override
    @Transactional
    public BranchResponse createBranch(CreateBranchRequest request) {
        log.info("Creating branch: {}", request.getName());

        if (branchRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Branch", "code", request.getCode());
        }

        Branch branch = branchMapper.toEntity(request);
        branch.setActive(true);

        branch = branchRepository.save(branch);
        log.info("Branch created successfully: {}", branch.getName());

        return branchMapper.toResponse(branch);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "branches", key = "#id")
    public BranchResponse getBranchById(Long id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch", "id", id));
        return branchMapper.toResponse(branch);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BranchResponse> getAllBranches(Pageable pageable) {
        return branchRepository.findAllNotDeleted(pageable)
                .map(branchMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BranchResponse> getActiveBranches() {
        return branchRepository.findAllActiveBranches()
                .stream()
                .map(branchMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @CacheEvict(value = "branches", key = "#id")
    public BranchResponse updateBranch(Long id, UpdateBranchRequest request) {
        log.info("Updating branch: {}", id);

        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch", "id", id));

        branchMapper.updateEntityFromRequest(request, branch);

        branch = branchRepository.save(branch);
        log.info("Branch updated successfully: {}", branch.getName());

        return branchMapper.toResponse(branch);
    }

    @Override
    @Transactional
    @CacheEvict(value = "branches", key = "#id")
    public void deleteBranch(Long id) {
        log.info("Soft deleting branch: {}", id);

        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch", "id", id));

        branch.setDeletedAt(LocalDateTime.now());
        branch.setActive(false);
        branchRepository.save(branch);

        log.info("Branch soft deleted: {}", branch.getName());
    }

    @Override
    @Transactional
    @CacheEvict(value = "branches", key = "#id")
    public void deactivateBranch(Long id) {
        log.info("Deactivating branch: {}", id);

        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch", "id", id));

        branch.setActive(false);
        branchRepository.save(branch);

        log.info("Branch deactivated: {}", branch.getName());
    }
}