package com.smart.service.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "categories")
@Data @NoArgsConstructor @AllArgsConstructor
public class CategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    private String name;
    private String description;
    @ManyToOne(fetch = FetchType.LAZY) // One ADMIN user can create many categories
    @JoinColumn(name = "user_id", nullable = false) // Each category belongs to one user
    @JsonIgnore   // IMPORTANT to avoid serialization loop
    private UserEntity users;

}
