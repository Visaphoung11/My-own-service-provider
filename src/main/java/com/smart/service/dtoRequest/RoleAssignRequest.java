package com.smart.service.dtoRequest;

import lombok.Data;

@Data
public class RoleAssignRequest {
    private Long userId;
    private Long roleId;
}