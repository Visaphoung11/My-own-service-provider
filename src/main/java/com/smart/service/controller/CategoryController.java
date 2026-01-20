package com.smart.service.controller;


import com.smart.service.dtoRequest.CategoryRequest;
import com.smart.service.entity.CategoryEntity;
import com.smart.service.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/categories")
    @PreAuthorize("hasRole('ADMIN')") // simple role check before entering the method
    public ResponseEntity<CategoryEntity> create (@RequestBody CategoryRequest categoryRequest, Principal principal){
        CategoryEntity createdCategory = categoryService.createCategory(categoryRequest, principal.getName());

        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
    }


}
