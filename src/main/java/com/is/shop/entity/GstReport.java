package com.is.shop.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "gst_reports")
public class GstReport extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

    @Column(name = "report_type", length = 50)
    public String reportType;

    @Column(name = "period_start")
    public LocalDate periodStart;

    @Column(name = "period_end")
    public LocalDate periodEnd;

    @CreationTimestamp // Automatically set on creation
    @Column(name = "generated_at", updatable = false)
    public LocalDateTime generatedAt;

    @Column(name = "total_sales", precision = 10, scale = 2)
    public BigDecimal totalSales;

    @Column(name = "total_purchases", precision = 10, scale = 2)
    public BigDecimal totalPurchases;

    @Column(name = "total_tax_collected", precision = 10, scale = 2)
    public BigDecimal totalTaxCollected;

    @Column(name = "total_itc_claimed", precision = 10, scale = 2)
    public BigDecimal totalItcClaimed; // Input Tax Credit
}