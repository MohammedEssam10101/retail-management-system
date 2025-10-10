package com.retail.management.mapper;

import com.retail.management.dto.request.customer.CreateCustomerRequest;
import com.retail.management.dto.request.customer.UpdateCustomerRequest;
import com.retail.management.dto.response.customer.CustomerResponse;
import com.retail.management.entity.Customer;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CustomerMapper {

    CustomerResponse toResponse(Customer customer);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customerCode", ignore = true)
    @Mapping(target = "isWalkIn", constant = "false")
    @Mapping(target = "loyaltyPoints", constant = "0")
    Customer toEntity(CreateCustomerRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customerCode", ignore = true)
    @Mapping(target = "isWalkIn", ignore = true)
    @Mapping(target = "loyaltyPoints", ignore = true)
    void updateEntityFromRequest(UpdateCustomerRequest request, @MappingTarget Customer customer);
}