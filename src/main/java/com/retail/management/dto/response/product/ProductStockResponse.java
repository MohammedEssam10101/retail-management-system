package com.retail.management.dto.response.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductStockResponse {
    private Long productId;
    private String productName;
    private String sku;
    private Integer totalQuantity;
    private Integer totalReserved;
    private Integer totalAvailable;
    private Boolean lowStock;
}