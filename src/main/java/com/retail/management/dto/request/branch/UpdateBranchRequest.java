package com.retail.management.dto.request.branch;

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
public class UpdateBranchRequest {

    @Size(max = 100)
    private String name;

    private String address;

    @Size(max = 50)
    private String city;

    @Size(max = 20)
    private String phone;

    @Email(message = "Email must be valid")
    @Size(max = 100)
    private String email;

    private Boolean active;
}
