package com.retail.management.repository.specification;

import com.retail.management.entity.Customer;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CustomerSpecification {

    public static Specification<Customer> filterCustomers(
            String searchTerm,
            String city,
            Boolean isWalkIn,
            BigDecimal minLoyaltyPoints,
            BigDecimal maxLoyaltyPoints) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter out soft-deleted customers
            predicates.add(criteriaBuilder.isNull(root.get("deletedAt")));

            // Search term (name, email, phone)
            if (searchTerm != null && !searchTerm.isEmpty()) {
                String likePattern = "%" + searchTerm.toLowerCase() + "%";
                Predicate firstNamePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("firstName")), likePattern);
                Predicate lastNamePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("lastName")), likePattern);
                Predicate emailPredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("email")), likePattern);
                Predicate phonePredicate = criteriaBuilder.like(
                        root.get("phone"), likePattern);
                predicates.add(criteriaBuilder.or(
                        firstNamePredicate, lastNamePredicate, emailPredicate, phonePredicate));
            }

            // Filter by city
            if (city != null && !city.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("city"), city));
            }

            // Filter by walk-in status
            if (isWalkIn != null) {
                predicates.add(criteriaBuilder.equal(root.get("isWalkIn"), isWalkIn));
            }

            // Filter by loyalty points range
            if (minLoyaltyPoints != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("loyaltyPoints"), minLoyaltyPoints));
            }
            if (maxLoyaltyPoints != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("loyaltyPoints"), maxLoyaltyPoints));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Customer> isRegistered() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("isWalkIn"), false);
    }

    public static Specification<Customer> hasCity(String city) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("city"), city);
    }

    public static Specification<Customer> loyaltyPointsGreaterThan(BigDecimal points) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThan(root.get("loyaltyPoints"), points);
    }

    public static Specification<Customer> isNotDeleted() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isNull(root.get("deletedAt"));
    }

    public static Specification<Customer> emailContains(String email) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("email")),
                        "%" + email.toLowerCase() + "%"
                );
    }
}