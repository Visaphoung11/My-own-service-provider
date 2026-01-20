package com.smart.service.service;

import com.smart.service.dtoRequest.CategoryRequest;
import com.smart.service.entity.CategoryEntity;

import java.util.List;

public interface CategoryService {

    List<CategoryEntity> getAllCategories();

    CategoryEntity getCategoryById(Long categoryId);


    CategoryEntity createCategory(CategoryRequest request, String email);


    CategoryEntity updateCategory(Long categoryId, CategoryRequest request, String email);

    void deleteCategory(Long categoryId, String email);
}
