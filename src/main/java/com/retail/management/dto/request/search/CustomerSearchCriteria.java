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
public class CustomerSearchCriteria {
    private String searchTerm;
    private String city;
    private Boolean isWalkIn;
    private BigDecimal minLoyaltyPoints;
    private BigDecimal maxLoyaltyPoints;
}