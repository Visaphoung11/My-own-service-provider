package com.smart.service.controller;


import com.smart.service.dtoRequest.CategoryRequest;
import com.smart.service.entity.CategoryEntity;
import com.smart.service.service.CategoryService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // simple role check before entering the method
    public ResponseEntity<@NonNull CategoryEntity> create (@RequestBody CategoryRequest categoryRequest, Principal principal){
        CategoryEntity createdCategory = categoryService.createCategory(categoryRequest, principal.getName());

        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
    } // @NonNull It tells the IDE that categoryService will never return null (because I used .orElseThrow()).

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<@NonNull CategoryEntity> updateCategory(
            @PathVariable Long id,
            @RequestBody CategoryRequest request,
            Principal principal) {

        return ResponseEntity.ok(
                categoryService.updateCategory(id, request, principal.getName())
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity< @NonNull Map<String, Object>> deleteCategory(
            @PathVariable Long id,
            Principal principal) {

        categoryService.deleteCategory(id, principal.getName());

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "Category deleted successfully");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
    @GetMapping
    public ResponseEntity< @NonNull List<CategoryEntity>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<@NonNull CategoryEntity> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

}
