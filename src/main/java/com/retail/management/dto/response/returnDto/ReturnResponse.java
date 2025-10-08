package com.retail.management.dto.response.returnDto;

import com.retail.management.enums.ReturnStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnResponse {
    private Long id;
    private String returnNumber;
    private Long invoiceId;
    private String invoiceNumber;
    private Long branchId;
    private String branchName;
    private LocalDate returnDate;
    private BigDecimal totalReturnAmount;
    private String returnReason;
    private String notes;
    private ReturnStatus status;
    private String processedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}