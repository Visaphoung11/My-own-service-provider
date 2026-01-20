package com.smart.service.repository;


import com.smart.service.enums.enums;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.smart.service.entity.RoleEntity;

@Repository
public interface RoleRepository extends CrudRepository<RoleEntity, Long> {
    RoleEntity findByName(enums name);}