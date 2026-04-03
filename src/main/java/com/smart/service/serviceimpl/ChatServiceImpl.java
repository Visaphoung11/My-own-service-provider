package com.smart.service.serviceimpl;

import com.smart.service.dtoRequest.ChatMessageRequest;
import com.smart.service.dtoResponse.ChatMessageResponse;
import com.smart.service.dtoResponse.ConversationResponse;
import com.smart.service.dtoResponse.UserProfileResponse;
import com.smart.service.entity.ChatMessageEntity;
import com.smart.service.entity.UserEntity;
import com.smart.service.repository.ChatMessageRepository;
import com.smart.service.repository.UserRepository;
import com.smart.service.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final com.smart.service.mapper.UserMapper userMapper;

    @Override
    @Transactional
    public ChatMessageResponse saveMessage(ChatMessageRequest request, String senderEmail) {
        UserEntity managedSender = userRepository.findByEmail(senderEmail);
        if (managedSender == null) {
            throw new RuntimeException("Sender not found");
        }

        UserEntity recipient = userRepository.findById(request.getRecipientId())
                .orElseThrow(() -> new RuntimeException("Recipient not found"));

        ChatMessageEntity entity = ChatMessageEntity.builder()
                .sender(managedSender)
                .recipient(recipient)
                .content(request.getContent())
                .type(request.getType())
                .mediaUrl(request.getMediaUrl())
                .timestamp(LocalDateTime.now())
                .isRead(false)
                .build();

        ChatMessageEntity saved = chatMessageRepository.save(entity);
        
        // Update lastActiveAt for both
        userRepository.updateLastActiveByEmail(senderEmail, LocalDateTime.now());
        userRepository.updateLastActiveByEmail(recipient.getEmail(), LocalDateTime.now());
        
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public List<ChatMessageResponse> getConversation(Long user1Id, Long user2Id) {
        return chatMessageRepository.findConversation(user1Id, user2Id)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<ConversationResponse> getMyConversations(Long userId) {
        List<UserEntity> recipients = chatMessageRepository.findRecipientsBySenderId(userId);
        List<UserEntity> senders = chatMessageRepository.findSendersByRecipientId(userId);
        
        java.util.Set<UserEntity> participants = new java.util.HashSet<>(recipients);
        participants.addAll(senders);
        
        return participants.stream()
                .map(user -> {
                    LocalDateTime lastMessageTime = chatMessageRepository.findLastMessageTime(userId, user.getId());
                    
                    // Offline if lastActiveAt is > 5 mins ago
                    boolean effectivelyOnline = Boolean.TRUE.equals(user.getIsOnline()) && 
                            user.getLastActiveAt() != null && 
                            user.getLastActiveAt().isAfter(LocalDateTime.now().minusMinutes(5));

                    return ConversationResponse.builder()
                            .userId(user.getId())
                            .email(user.getEmail())
                            .fullName(user.getFullName())
                            .profileImage(user.getProfileImage())
                            .lastMessageTime(lastMessageTime != null ? lastMessageTime.toString() : null)
                            .isOnline(effectivelyOnline)
                            .lastActiveAt(user.getLastActiveAt() != null ? user.getLastActiveAt().toString() : null)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private ChatMessageResponse mapToResponse(ChatMessageEntity entity) {
        return ChatMessageResponse.builder()
                .id(entity.getId())
                .senderId(entity.getSender().getId())
                .senderName(entity.getSender().getFullName())
                .senderEmail(entity.getSender().getEmail())
                .recipientId(entity.getRecipient().getId())
                .recipientName(entity.getRecipient().getFullName())
                .recipientEmail(entity.getRecipient().getEmail())
                .content(entity.getContent())
                .type(entity.getType())
                .mediaUrl(entity.getMediaUrl())
                .timestamp(entity.getTimestamp().toString())
                .isRead(entity.isRead())
                .build();
    }
}
