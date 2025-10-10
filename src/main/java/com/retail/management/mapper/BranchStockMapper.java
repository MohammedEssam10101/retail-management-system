package com.retail.management.mapper;

import com.retail.management.dto.response.branch.BranchStockResponse;
import com.retail.management.entity.BranchStock;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BranchStockMapper {

    @Mapping(target = "branchId", source = "branch.id")
    @Mapping(target = "branchName", source = "branch.name")
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "availableQuantity", expression = "java(calculateAvailable(branchStock))")
    BranchStockResponse toResponse(BranchStock branchStock);

    default Integer calculateAvailable(BranchStock branchStock) {
        return branchStock.getQuantity() - branchStock.getReservedQuantity();
    }
}