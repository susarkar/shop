package com.is.shop.dto;

import com.is.shop.entity.Category;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CategoryDto {
    public Integer id;

    public String name;

    public CategoryDto() {
    }

    public CategoryDto(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
    public static CategoryDto fromEntity(Category category){
        if(category == null) return null;
        return new CategoryDto(category.id, category.name);
    }
    public static Category toEntity(CategoryDto categoryDto){
        if(categoryDto == null) return null;
        Category category = new Category();
        category.id = categoryDto.id;
        category.name = categoryDto.name;
        return category;
    }



}
