package com.is.shop.dto;

import com.is.shop.entity.Product;

import java.math.BigDecimal;

public class ProductDto {
    public Integer id;
    public String name;
    public String sku;
    public BigDecimal purchasePrice;
    public BigDecimal salePrice;
    public Integer stockQuantity;
    public BigDecimal taxRate;
    public String barcode;
    public Integer categoryId;

    public ProductDto() {
    }

    public ProductDto(Integer id, String name, String sku, BigDecimal purchasePrice, BigDecimal salePrice, Integer stockQuantity, BigDecimal taxRate, String barcode, Integer categoryId) {
        this.id = id;
        this.name = name;
        this.sku = sku;
        this.purchasePrice = purchasePrice;
        this.salePrice = salePrice;
        this.stockQuantity = stockQuantity;
        this.taxRate = taxRate;
        this.barcode = barcode;
        this.categoryId = categoryId;
    }

    public static ProductDto fromEntity(Product product){
       if(product == null) return null;
       return new ProductDto(product.id, product.name, product.sku, product.purchasePrice, product.salePrice, product.stockQuantity, product.taxRate, product.barcode, product.category.id);

    }
}
