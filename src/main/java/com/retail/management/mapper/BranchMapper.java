package com.retail.management.mapper;

import com.retail.management.dto.request.branch.CreateBranchRequest;
import com.retail.management.dto.request.branch.UpdateBranchRequest;
import com.retail.management.dto.response.branch.BranchResponse;
import com.retail.management.entity.Branch;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BranchMapper {

    BranchResponse toResponse(Branch branch);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    Branch toEntity(CreateBranchRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    void updateEntityFromRequest(UpdateBranchRequest request, @MappingTarget Branch branch);
}