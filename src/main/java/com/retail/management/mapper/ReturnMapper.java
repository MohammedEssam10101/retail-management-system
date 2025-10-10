package com.retail.management.mapper;

import com.retail.management.dto.response.returnDto.ReturnResponse;
import com.retail.management.entity.Return;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReturnMapper {

    @Mapping(target = "invoiceId", source = "invoice.id")
    @Mapping(target = "invoiceNumber", source = "invoice.invoiceNumber")
    @Mapping(target = "branchId", source = "branch.id")
    @Mapping(target = "branchName", source = "branch.name")
    @Mapping(target = "processedBy", expression = "java(getProcessedByName(returnEntity))")
    ReturnResponse toResponse(Return returnEntity);

    default String getProcessedByName(Return returnEntity) {
        if (returnEntity.getProcessedBy() != null) {
            return returnEntity.getProcessedBy().getFirstName() + " " +
                    returnEntity.getProcessedBy().getLastName();
        }
        return null;
    }
}