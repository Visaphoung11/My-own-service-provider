package com.smart.service.repository;

import com.smart.service.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByEmail (String email);
    UserEntity findById (long id);
    boolean existsByEmail(String email);

    boolean findByPassword(String password);
}
