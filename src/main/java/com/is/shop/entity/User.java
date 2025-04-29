package com.is.shop.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

    @Column(length = 100, unique = true, nullable = false)
    public String username;

    @Column(name = "password_hash", length = 255, nullable = false)
    public String passwordHash; // Store hashed passwords only!

    @Column(length = 255)
    public String name;

    @Column(length = 50)
    public String role;

    @Column(length = 20)
    public String status = "active"; // Match default value
}