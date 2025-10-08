package com.retail.management.repository;

import com.retail.management.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>,
        JpaSpecificationExecutor<Product> {

    Optional<Product> findBySku(String sku);

    Boolean existsBySku(String sku);

    List<Product> findByActiveTrue();

    @Query("SELECT p FROM Product p WHERE p.active = true AND p.deletedAt IS NULL")
    List<Product> findAllActiveProducts();

    @Query("SELECT p FROM Product p WHERE p.active = true AND p.deletedAt IS NULL")
    Page<Product> findAllActiveProducts(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.category = :category AND p.active = true AND p.deletedAt IS NULL")
    List<Product> findByCategory(@Param("category") String category);

    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.active = true AND p.deletedAt IS NULL")
    List<String> findAllCategories();

    @Query("SELECT p FROM Product p WHERE p.active = true AND p.deletedAt IS NULL AND (" +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(p.sku) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(p.category) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Product> searchProducts(@Param("searchTerm") String searchTerm, Pageable pageable);
}
