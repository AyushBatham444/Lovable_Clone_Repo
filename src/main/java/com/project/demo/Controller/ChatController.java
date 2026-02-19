package com.project.demo.Controller;

import com.project.demo.Service.AiGenerationService;
import com.project.demo.Service.ChatService;
import com.project.demo.dto.chat.ChatRequest;
import com.project.demo.dto.chat.ChatResponse;
import com.project.demo.dto.chat.StreamResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.awt.*;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final AiGenerationService aiGenerationService;
    private final ChatService chatService;

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    // This will help send data in stream rather than one go just like in chat gpt
    //openai dependency has two model either once or in stream
    public Flux<ServerSentEvent<StreamResponse>> streamChat(
            @RequestBody ChatRequest request
    )
    {
         return aiGenerationService.streamResponse(request.message(),request.projectId())
                 .map(data-> ServerSentEvent.<StreamResponse>builder()
                         .data(data)
                         .build());

    }

    @GetMapping("/projects/{projectId}")
    public ResponseEntity<List<ChatResponse>> getChatHistory(
            @PathVariable Long projectId
    )
    {
        return ResponseEntity.ok(chatService.getProjectChatHistory(projectId));
    }

}

