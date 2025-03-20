package com.onelab.users_service.entity;

import com.onelab.users_service.repository.jpa.constants.MessageConstants;
import jakarta.persistence.*;
import lombok.*;
import org.onelab.common.enums.MessageState;
import org.onelab.common.enums.MessageType;

@Entity
@Table(name = "messages")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
@NamedQuery(name = MessageConstants.FIND_MESSAGES_BY_CHAT_ID,
        query = "SELECT m FROM Message m WHERE m.chat.id = :chatId ORDER BY m.createdAt"
)
@NamedQuery(name = MessageConstants.SET_MESSAGES_TO_SEEN_BY_CHAT,
        query = "UPDATE Message SET messageState = :newState WHERE chat.id = :chatId"
)
public class Message extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    @Enumerated(EnumType.STRING)
    private MessageState messageState;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Column(name = "receiver_id", nullable = false)
    private Long receiverId;

    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @Column(name = "resource_id")
    private Long resourceId;
}
