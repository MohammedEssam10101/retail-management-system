package com.retail.management.entity;

import com.retail.management.enums.ReturnStatus;
import lombok.*;
import jakarta.persistence.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "returns")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Return extends BaseEntity {

    @Column(name = "return_number", nullable = false, unique = true, length = 50)
    private String returnNumber;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processed_by", nullable = false)
    private User processedBy;

    @Column(name = "return_date", nullable = false)
    private LocalDate returnDate;

    @Column(name = "total_return_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalReturnAmount = BigDecimal.ZERO;

    @Column(name = "return_reason", length = 100)
    private String returnReason;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ReturnStatus status = ReturnStatus.PENDING;

    @OneToMany(mappedBy = "returnRecord", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ReturnItem> items = new ArrayList<>();

    public void addItem(ReturnItem item) {
        items.add(item);
        item.setReturnRecord(this);
    }

    public void removeItem(ReturnItem item) {
        items.remove(item);
        item.setReturnRecord(null);
    }
}
