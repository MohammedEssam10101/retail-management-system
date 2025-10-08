package com.retail.management.dto.response.report;

import com.retail.management.enums.DiscountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountSummaryDTO {
    private DiscountType discountType;
    private BigDecimal totalAmount;
    private Long usageCount;
    private BigDecimal averageDiscount;
}