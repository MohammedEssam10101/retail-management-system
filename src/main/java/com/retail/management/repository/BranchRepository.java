package com.retail.management.repository;

import com.retail.management.entity.Branch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {

    Optional<Branch> findByCode(String code);

    Boolean existsByCode(String code);

    List<Branch> findByActiveTrue();

    @Query("SELECT b FROM Branch b WHERE b.active = true AND b.deletedAt IS NULL")
    List<Branch> FindAllActiveBranches();

    @Query("SELECT b FROM Branch b WHERE b.deletedAt IS NULL")
    Page<Branch> findAllNotDeleted(Pageable pageable);

    @Query("SELECT b FROM Branch b WHERE b.deletedAt IS NULL AND b.active = true AND b.city = :city")
    List<Branch> findActiveBranchesByCity(@Param("city") String city);

    @Query("SELECT b FROM Branch b WHERE b.deletedAt IS NULL AND (" +
            "LOWER(b.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(b.code) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(b.city) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Branch> searchBranches(@Param("searchTerm") String searchTerm, Pageable pageable);
}
