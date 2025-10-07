package com.retail.management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "branches")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Branch extends BaseEntity{

    @Column(name = "code", nullable = false, unique = true, length = 20)
    private String code;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "city", length = 50)
    private String city;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL ,fetch = FetchType.LAZY)
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL ,fetch = FetchType.LAZY)
    private List<BranchStock> stocks = new ArrayList<>();

    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL ,fetch = FetchType.LAZY)
    private List<Invoice> invoices = new ArrayList<>();

    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL ,fetch = FetchType.LAZY)
    private List<StockAdjustment> stockAdjustments = new ArrayList<>();
}
