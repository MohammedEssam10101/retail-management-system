package com.retail.management.dto.request.returnDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReturnRequest {

    @NotNull(message = "Invoice ID is required")
    private Long invoiceId;

    @NotEmpty(message = "Return must have at least one item")
    @Valid
    private List<ReturnItemRequest> items;

    private String returnReason;

    private String notes;
}