package com.retail.management.dto.response.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LowStockItem {
    private Long productId;
    private String productName;
    private String sku;
    private Integer currentQuantity;
    private Integer lowStockThreshold;
    private Integer quantityNeeded;
}
