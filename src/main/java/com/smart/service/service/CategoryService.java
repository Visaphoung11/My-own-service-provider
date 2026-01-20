package com.smart.service.service;

import com.smart.service.dtoRequest.CategoryRequest;
import com.smart.service.entity.CategoryEntity;

import java.util.List;

public interface CategoryService {

    List<CategoryService> getAllCategories();
    CategoryEntity getCategoryById (Long categoryId);


    CategoryEntity createCategory(CategoryRequest request, String email);


    CategoryEntity updateCategory (CategoryEntity categoryEntity);
    void deleteCategory (Long categoryId);
}
