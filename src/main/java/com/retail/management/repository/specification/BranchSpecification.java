package com.retail.management.repository.specification;

import com.retail.management.entity.Branch;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class BranchSpecification {

    public static Specification<Branch> filterBranches(
            String searchTerm,
            String city,
            Boolean active) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter out soft-deleted branches
            predicates.add(criteriaBuilder.isNull(root.get("deletedAt")));

            // Search term (name, code, city)
            if (searchTerm != null && !searchTerm.isEmpty()) {
                String likePattern = "%" + searchTerm.toLowerCase() + "%";
                Predicate namePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")), likePattern);
                Predicate codePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("code")), likePattern);
                Predicate cityPredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("city")), likePattern);
                predicates.add(criteriaBuilder.or(namePredicate, codePredicate, cityPredicate));
            }

            // Filter by city
            if (city != null && !city.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("city"), city));
            }

            // Filter by active status
            if (active != null) {
                predicates.add(criteriaBuilder.equal(root.get("active"), active));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Branch> isActive() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.equal(root.get("active"), true),
                        criteriaBuilder.isNull(root.get("deletedAt"))
                );
    }

    public static Specification<Branch> hasCity(String city) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("city"), city);
    }

    public static Specification<Branch> isNotDeleted() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isNull(root.get("deletedAt"));
    }
}
