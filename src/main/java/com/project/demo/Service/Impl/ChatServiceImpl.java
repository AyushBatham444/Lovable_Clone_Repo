package com.project.demo.Service.Impl;

import com.project.demo.Entity.ChatMessages;
import com.project.demo.Entity.ChatSession;
import com.project.demo.Entity.enums.ChatSessionId;
import com.project.demo.Service.ChatService;
import com.project.demo.dto.chat.ChatResponse;
import com.project.demo.mapper.ChatMapper;
import com.project.demo.repository.ChatMessageRepository;
import com.project.demo.repository.ChatSessionRepository;
import com.project.demo.security.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {

    private final ChatMessageRepository chatMessageRepositoryObj;
    private final AuthUtil authUtil;
    private final ChatSessionRepository chatSessionRepositoryObj;
    private final ChatMapper chatMapper;

    @Override
    public List<ChatResponse> getProjectChatHistory(Long projectId) {

        Long userId= authUtil.getCurrentUserId();

        ChatSession chatSession=chatSessionRepositoryObj.getReferenceById(
                new ChatSessionId(projectId,userId)
        );

        List<ChatMessages> chatMessagesList=chatMessageRepositoryObj.findByChatSession(chatSession);
        return chatMapper.fromListOfChatMessage(chatMessagesList);

    }
}
