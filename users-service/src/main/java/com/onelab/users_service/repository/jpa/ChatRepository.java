package com.onelab.users_service.repository.jpa;

import com.onelab.users_service.entity.Chat;
import com.onelab.users_service.repository.jpa.constants.ChatConstants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, String> {
    @Query(name = ChatConstants.FIND_CHAT_BY_SENDER_ID)
    List<Chat> findChatsBySenderId(@Param("senderId") Long userId);

    @Query(name = ChatConstants.FIND_CHAT_BY_SENDER_ID_AND_RECEIVER)
    Optional<Chat> findChatBySenderAndReceiver(@Param("senderId") Long senderId, @Param("recipientId") Long receiverId);
}
