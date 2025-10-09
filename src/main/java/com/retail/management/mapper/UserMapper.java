package com.retail.management.mapper;

import com.retail.management.dto.request.user.CreateUserRequest;
import com.retail.management.dto.request.user.UpdateUserRequest;
import com.retail.management.dto.response.user.UserResponse;
import com.retail.management.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "roleName", source = "role.name")
    @Mapping(target = "branchId", source = "branch.id")
    @Mapping(target = "branchName", source = "branch.name")
    UserResponse toResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "branch", ignore = true)
    @Mapping(target = "password", ignore = true)
    User toEntity(CreateUserRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "branch", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateEntityFromRequest(UpdateUserRequest request, @MappingTarget User user);
}