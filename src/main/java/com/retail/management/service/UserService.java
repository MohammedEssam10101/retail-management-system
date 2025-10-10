package com.retail.management.service;

import com.retail.management.dto.request.user.CreateUserRequest;
import com.retail.management.dto.request.user.UpdateUserRequest;
import com.retail.management.dto.response.user.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponse createUser(CreateUserRequest request);
    UserResponse getUserById(Long id);
    Page<UserResponse> getAllUsers(Pageable pageable);
    Page<UserResponse> getUsersByBranch(Long branchId, Pageable pageable);
    UserResponse updateUser(Long id, UpdateUserRequest request);
    void deleteUser(Long id);
    Page<UserResponse> searchUsers(String searchTerm, Pageable pageable);
}