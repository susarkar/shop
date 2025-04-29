package com.is.shop.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "sale_items")
public class SaleItem extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) // A sale item must belong to a sale
    @JoinColumn(name = "sale_id", nullable = false)
    public Sale sale;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) // A sale item must refer to a product
    @JoinColumn(name = "product_id", nullable = false)
    public Product product;

    @Column(nullable = false)
    public Integer quantity;

    @Column(precision = 10, scale = 2, nullable = false)
    public BigDecimal price; // Price per unit at the time of sale

    @Column(name = "tax_rate", precision = 5, scale = 2)
    public BigDecimal taxRate; // Tax rate applied to this item at the time of sale
}