package com.smart.service.dtoResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationResponse {
    private Long userId;
    private String email;
    private String fullName;
    private String profileImage;
    private String lastMessageTime;
    private boolean isOnline;
    private String lastActiveAt;
}
