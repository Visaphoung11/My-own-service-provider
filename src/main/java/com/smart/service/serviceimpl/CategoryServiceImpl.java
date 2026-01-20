package com.smart.service.serviceimpl;

import com.smart.service.dtoRequest.CategoryRequest;
import com.smart.service.entity.CategoryEntity;
import com.smart.service.entity.UserEntity;
import com.smart.service.enums.enums;
import com.smart.service.exception.ForbiddenException;
import com.smart.service.exception.ResourceNotFoundException;
import com.smart.service.repository.CategoryRepository;
import com.smart.service.repository.UserRepository;
import com.smart.service.service.CategoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor

public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;


    @Override
    public List<CategoryEntity> getAllCategories() {

        return categoryRepository.findAll();
    }

    @Override
    public CategoryEntity getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }


    @Override
    public CategoryEntity createCategory(CategoryRequest request, String email) {

        UserEntity users = userRepository.findByEmail(email);


        boolean isAdmin = users.getRoles().stream()
                .anyMatch(role -> role.getName() == enums.ADMIN);
        /*
         *.anyMatch() is a Stream method in Java
         * It checks if at least one element in a collection matches a condition
         * Returns a boolean (true or false)
         */
         if (!isAdmin){
             throw new ForbiddenException("You are not allowed to create this category");

         }

        CategoryEntity entity = new CategoryEntity();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setUsers(users);

        return categoryRepository.save(entity);
    }

    @Override
    public CategoryEntity updateCategory(Long categoryId, CategoryRequest request, String email) {

        UserEntity users = userRepository.findByEmail(email);
        CategoryEntity category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        boolean isAdmin = users.getRoles().stream()
                .anyMatch(role -> role.getName() == enums.ADMIN);
        if (!isAdmin){
            throw new ForbiddenException("You are not allowed to update this category");

        }
        category.setName(request.getName());
        category.setDescription(request.getDescription());

        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Long categoryId, String email) {
        // Find the user making the request
        UserEntity users = userRepository.findByEmail(email);

        // Find category by id to delete, if not found will throw ResourceNotFoundException
        CategoryEntity category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        // Check if the user is ADMIN
        boolean isAdmin = users.getRoles().stream()
                .anyMatch(role -> role.getName() == enums.ADMIN);
        if (!isAdmin){
            throw new ForbiddenException("You are not allowed to delete this category");

        }
     categoryRepository.delete(category);
    }


}
