package com.project.demo.mapper;

import com.project.demo.Entity.ChatMessages;
import com.project.demo.dto.chat.ChatResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChatMapper {

    List<ChatResponse> fromListOfChatMessage(List<ChatMessages> chatMessagesList);
}
