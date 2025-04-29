package com.is.shop.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "purchase_items")
public class PurchaseItem extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) // Must belong to a purchase
    @JoinColumn(name = "purchase_id", nullable = false)
    public Purchase purchase;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) // Must refer to a product
    @JoinColumn(name = "product_id", nullable = false)
    public Product product;

    @Column(nullable = false)
    public Integer quantity;

    @Column(precision = 10, scale = 2, nullable = false)
    public BigDecimal price; // Purchase price per unit

    @Column(name = "tax_rate", precision = 5, scale = 2)
    public BigDecimal taxRate; // Tax rate applied at time of purchase
}