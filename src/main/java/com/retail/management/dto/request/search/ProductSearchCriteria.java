package com.retail.management.dto.request.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchCriteria {
    private String searchTerm;
    private String category;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Boolean active;
}