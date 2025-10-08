package com.retail.management.dto.request.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    @Email(message = "Email must be valid")
    private String email;

    @Size(min = 6)
    private String password;

    private String firstName;

    private String lastName;

    private String phone;

    private Long roleId;

    private Long branchId;

    private Boolean active;
}