package com.project.demo.Service.Impl;

import com.project.demo.Entity.*;
import com.project.demo.Entity.enums.ChatEventType;
import com.project.demo.Entity.enums.ChatSessionId;
import com.project.demo.Entity.enums.MessageRole;
import com.project.demo.Service.AiGenerationService;
import com.project.demo.Service.ProjectFileService;
import com.project.demo.Service.UsageService;
import com.project.demo.dto.chat.StreamResponse;
import com.project.demo.errors.ResourceNotFoundException;
import com.project.demo.llm.LlmResponseParser;
import com.project.demo.llm.PromptUtils;
import com.project.demo.llm.advisors.FileTreeContextAdvisor;
import com.project.demo.llm.tools.CodeGenerationTools;
import com.project.demo.repository.*;
import com.project.demo.security.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class AiGenerationServiceImpl implements AiGenerationService {

    private final ChatClient chatClient;
    private final AuthUtil authUtil;
    private final ProjectFileService projectFileService;
    private final FileTreeContextAdvisor fileTreeContextAdvisor;
    private final LlmResponseParser llmResponseParser;
    private final ChatSessionRepository chatSessionRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatEventRepository chatEventRepository;
    private final UsageService usageService;

    private static final Pattern FILE_TAG_PATTERN= Pattern.compile("<file path=\"([^\"]+)\">(.*?)</file>" ,Pattern.DOTALL); // this helps to parse file structure

    @Override
    @PreAuthorize("@security.canEditProject(#projectId)") // userid will be taken from security context
    public Flux<StreamResponse> streamResponse(String userMessage, Long projectId) {

      //  usageService.checkDailyTokensUsage(); // won't allow generation if limit reached

        Long userId=authUtil.getCurrentUserId();
       ChatSession chatSession= createChatSessionIfNotExists(projectId,userId); // this auto create chat session just like in chatGpt

        Map<String,Object> advisorParams=Map.of("userId",userId,"projectId",projectId);

        StringBuilder fullResponseBuffer=new StringBuilder();

        CodeGenerationTools codeGenerationTools=new CodeGenerationTools(projectFileService,projectId);

        AtomicReference<Long> startTime=new AtomicReference<>(System.currentTimeMillis()); // To show the thinking for 5 sec etc ... as a response
        AtomicReference<Long> endTime =new AtomicReference<>(0L); // keeps end time in this thread only if new threads are present
        AtomicReference<Usage> usageRef = new AtomicReference<>();

        return chatClient.prompt()
                .system(PromptUtils.CODE_GENERATION_SYSTEM_PROMPT) // you need to make this code_Generation_system_prompt as static if u want to put it here
                .advisors(advisorSpec -> {
                    advisorSpec.params(advisorParams);
                    advisorSpec.advisors(fileTreeContextAdvisor);
                })
                .tools(codeGenerationTools)
                .user(userMessage)
                .stream()  // so we send streams not just one answer
                .chatResponse()// has many things like tools and other info but we just need the text back
                .doOnNext(response->{ // when on next chunk of same response
                        String content=response.getResult().getOutput().getText(); // its like i am can be first response then ayush 2nd I am a coder will be 3rd and so on so we will keep appending these chunks to string buider below
                    if(content != null && !content.isEmpty() && endTime.get()==0) // first non empty chunk received on doFirst u can get empty chunk as well
                    {
                        endTime.set(System.currentTimeMillis());
                    }
                    if(response.getMetadata().getUsage()!=null)
                    {
                        usageRef.set(response.getMetadata().getUsage());
                    }
                    fullResponseBuffer.append(content);
                })
                .doOnComplete(()->{ // when llm stop sending text that is all chunks are received for the current response
                    Schedulers.boundedElastic().schedule(()->{ // this function creates a seperate thread to perform the below function as below function is heavy and require resources
//                        parseAndSaveFiles(fullResponseBuffer.toString(),projectId);

                        long duration = (endTime.get()-startTime.get())/1000;
                        finalizeChats(userMessage,chatSession,fullResponseBuffer.toString(),duration,usageRef.get());
                    });

                })
                .doOnError(error->log.error("Error during Streaming for projectId"+ projectId))
                .map(response->{
                    String text= response.getResult().getOutput().getText();
                    return  new StreamResponse(text != null ? text: "");
                });


    }

    private void finalizeChats(String userMessage , ChatSession chatSession ,String fullText,Long duration,Usage usage){
        Long projectId=chatSession.getProject().getId();

        if(usage != null)
        {
            int totalTokens = usage.getTotalTokens();
            usageService.recordTokenUsage(chatSession.getUser().getId(),totalTokens);
        }

        //save the User Message
        chatMessageRepository.save(
                ChatMessages.builder()
                        .chatSession(chatSession)
                        .role(MessageRole.USER)
                        .content(userMessage)
                        .tokensUsed(usage.getPromptTokens())
                        .build()
        );

        ChatMessages assistantChatMessage=ChatMessages.builder()
                .role(MessageRole.ASSISTANT)
                .content("Assistant Message here .....")
                .chatSession(chatSession)
                .tokensUsed(usage.getCompletionTokens())
                .build();

        assistantChatMessage=chatMessageRepository.save(assistantChatMessage);

        List<ChatEvent> chatEventList = llmResponseParser.parseChatEvents(fullText,assistantChatMessage);
        chatEventList.addFirst(ChatEvent.builder()
                        .type(ChatEventType.THOUGHT)
                        .chatMessage(assistantChatMessage)
                        .content("Thought for "+duration+"s")
                        .sequenceOrder(0) // as rest starts from 1
                        .build());

        chatEventList.stream()
                .filter(e->e.getType()== ChatEventType.FILE_EDIT)
                .forEach(e-> projectFileService.saveFile(projectId,e.getFilePath(),e.getContent()));

        chatEventRepository.saveAll(chatEventList);
    }


    private ChatSession createChatSessionIfNotExists(Long projectId, Long userId) {

        ChatSessionId chatSessionId =new ChatSessionId(projectId,userId);
        ChatSession chatSession=chatSessionRepository.findById(chatSessionId).orElse(null);

        if(chatSession==null)
        {
            Project project=projectRepository.findById(projectId)
                    .orElseThrow(()-> new ResourceNotFoundException("Project",projectId.toString()));

            User user=userRepository.findById(userId)
                    .orElseThrow(()-> new ResourceNotFoundException("User",userId.toString()));

            chatSession =ChatSession.builder()
                    .id(chatSessionId)
                    .project(project)
                    .user(user)
                    .build();

            chatSession=chatSessionRepository.save(chatSession);
        }
        return chatSession;
    }
}
