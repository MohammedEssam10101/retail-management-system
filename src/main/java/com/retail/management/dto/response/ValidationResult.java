package com.retail.management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationResult {
    private Boolean valid;
    private List<String> errors;

    public static ValidationResult success() {
        return ValidationResult.builder()
                .valid(true)
                .errors(new ArrayList<>())
                .build();
    }

    public static ValidationResult failure(List<String> errors) {
        return ValidationResult.builder()
                .valid(false)
                .errors(errors)
                .build();
    }

    public static ValidationResult failure(String error) {
        List<String> errors = new ArrayList<>();
        errors.add(error);
        return ValidationResult.builder()
                .valid(false)
                .errors(errors)
                .build();
    }
}