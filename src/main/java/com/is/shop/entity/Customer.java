package com.is.shop.entity; // Adjust package name as needed

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "customers")
public class Customer extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

    @Column(length = 255)
    public String name;

    @Column(length = 20)
    public String phone;

    @Column(length = 255)
    public String email;

    @Column(length = 15)
    public String gstin; // Goods and Services Tax Identification Number

    @CreationTimestamp // Automatically set on creation
    @Column(name = "created_at", updatable = false)
    public LocalDateTime createdAt;

    // Optional: If you want to navigate from Customer to Sales
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<Sale> sales;
}