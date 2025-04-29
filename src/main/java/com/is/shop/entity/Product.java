package com.is.shop.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "products")
public class Product extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

    @Column(length = 255, nullable = false)
    public String name;

    @Column(length = 100, unique = true, nullable = false)
    public String sku;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id") // Matches the FOREIGN KEY column name
    public Category category;

    @Column(name = "purchase_price", precision = 10, scale = 2)
    public BigDecimal purchasePrice;

    @Column(name = "sale_price", precision = 10, scale = 2)
    public BigDecimal salePrice;

    @Column(name = "stock_quantity")
    public Integer stockQuantity = 0; // Match default value

    @Column(name = "tax_rate", precision = 5, scale = 2)
    public BigDecimal taxRate;

    @Column(length = 100)
    public String barcode;

    @CreationTimestamp // Automatically set on creation
    @Column(name = "created_at", updatable = false)
    public LocalDateTime createdAt;

    // Optional: Relationships to SaleItems and PurchaseItems
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<SaleItem> saleItems;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<PurchaseItem> purchaseItems;
}