package com.is.shop.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "suppliers")
public class Supplier extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

    @Column(length = 255)
    public String name;

    @Column(length = 20)
    public String phone;

    @Column(length = 255)
    public String email;

    @Column(columnDefinition = "TEXT")
    public String address;

    @Column(length = 15)
    public String gstin;

    @CreationTimestamp // Automatically set on creation
    @Column(name = "created_at", updatable = false)
    public LocalDateTime createdAt;

    // Optional: If you want to navigate from Supplier to Purchases
    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<Purchase> purchases;
}
