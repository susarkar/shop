package com.is.shop.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "expenses")
public class Expense extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

    @Column(length = 100)
    public String category;

    @Column(columnDefinition = "TEXT")
    public String description;

    @Column(precision = 10, scale = 2)
    public BigDecimal amount;

    @Column(name = "gst_applicable")
    public Boolean gstApplicable;

    @Column // JPA maps LocalDate to DATE SQL type by default
    public LocalDate date;

    @Column(name = "attachment_url", length = 255)
    public String attachmentUrl;
}