package com.is.shop.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "purchases")
public class Purchase extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    public Supplier supplier;

    @CreationTimestamp // Use @CreationTimestamp if 'date' should default to now on insert
    @Column(name = "date") // Or use LocalDateTime if you need to set it manually
    public LocalDateTime date;

    @Column(name = "total_amount", precision = 10, scale = 2)
    public BigDecimal totalAmount;

    @Column(name = "tax_amount", precision = 10, scale = 2)
    public BigDecimal taxAmount;

    @Column(length = 50)
    public String status;

    @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    public List<PurchaseItem> items;
}