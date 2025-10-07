package com.retail.management.repository.specification;

import com.retail.management.entity.User;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class UserSpecification {

    public static Specification<User> filterUsers(
            String searchTerm,
            Long branchId,
            Long roleId,
            Boolean active) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Search term (username, email, first name, last name)
            if (searchTerm != null && !searchTerm.isEmpty()) {
                String likePattern = "%" + searchTerm.toLowerCase() + "%";
                Predicate usernamePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("username")), likePattern);
                Predicate emailPredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("email")), likePattern);
                Predicate firstNamePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("firstName")), likePattern);
                Predicate lastNamePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("lastName")), likePattern);
                predicates.add(criteriaBuilder.or(
                        usernamePredicate, emailPredicate, firstNamePredicate, lastNamePredicate));
            }

            // Filter by branch
            if (branchId != null) {
                predicates.add(criteriaBuilder.equal(root.get("branch").get("id"), branchId));
            }

            // Filter by role
            if (roleId != null) {
                predicates.add(criteriaBuilder.equal(root.get("role").get("id"), roleId));
            }

            // Filter by active status
            if (active != null) {
                predicates.add(criteriaBuilder.equal(root.get("active"), active));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<User> isActive() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("active"), true);
    }

    public static Specification<User> hasBranch(Long branchId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("branch").get("id"), branchId);
    }

    public static Specification<User> hasRole(String roleName) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("role").get("name"), roleName);
    }
}