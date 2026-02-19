package com.project.demo.dto.chat;

import com.project.demo.Entity.enums.ChatEventType;


public record ChatEventResponse(
        Long id,
        ChatEventType type,
        Integer sequenceOrder,
        String content,
        String filePath,
        String metadata
) {
}
