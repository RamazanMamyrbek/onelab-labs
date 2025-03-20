package com.onelab.users_service.mapper;

import com.onelab.users_service.entity.Chat;
import com.onelab.users_service.entity.Message;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.onelab.common.dto.response.ChatResponseDto;
import org.onelab.common.dto.response.MessageResponseDto;

@Mapper(componentModel = "spring")
public interface ChatMapper {

    @Mapping(target = "name", source = "chat", qualifiedByName = "mapToChatName")
    @Mapping(target = "id", source = "chat.id")
    @Mapping(target = "senderId", source = "chat.sender.id")
    @Mapping(target = "receiverId", source = "chat.receiver.id")
    ChatResponseDto mapToChatResponseDto(Chat chat, @Context Long userId);

    @Named("mapToChatName")
    default String mapToChatName(Chat chat, @Context Long userId) {
        return chat.getChatName(userId);
    }

    @Mapping(target = "id", source = "message.id")
    @Mapping(target = "content", source = "message.content")
    @Mapping(target = "messageType", source = "message.messageType")
    @Mapping(target = "messageState", source = "message.messageState")
    @Mapping(target = "senderId", source = "message.senderId")
    @Mapping(target = "receiverId", source = "message.receiverId")
    @Mapping(target = "createdAt", source = "message.createdAt")
    MessageResponseDto toMessageResponse(Message message);
}
