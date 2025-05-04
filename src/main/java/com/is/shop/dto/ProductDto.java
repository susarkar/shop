package com.is.shop.dto;

import com.is.shop.entity.Product;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
@ApplicationScoped
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

    public static Product toEntity(ProductDto productDto) {
        if (productDto == null) return null;
        Product product = new Product();
        product.id = productDto.id;
        product.name = productDto.name;
        product.sku = productDto.sku;
        product.purchasePrice = productDto.purchasePrice;
        product.salePrice = productDto.salePrice;
        product.stockQuantity = productDto.stockQuantity;
        product.taxRate = productDto.taxRate;
        product.barcode = productDto.barcode;

        return product;

    }
}
