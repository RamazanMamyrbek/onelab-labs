package com.onelab.users_service.entity;

import com.onelab.users_service.repository.jpa.constants.ChatConstants;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "chats")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
@NamedQuery(name = ChatConstants.FIND_CHAT_BY_SENDER_ID,
        query = "SELECT DISTINCT c FROM Chat c WHERE c.sender.id = :senderId OR c.receiver.id = :senderId ORDER BY createdAt DESC"
)
@NamedQuery(name = ChatConstants.FIND_CHAT_BY_SENDER_ID_AND_RECEIVER,
        query = "SELECT DISTINCT c FROM Chat c WHERE (c.sender.id = :senderId AND c.receiver.id = :recipientId) OR (c.sender.id = :recipientId AND c.receiver.id = :senderId) ORDER BY createdAt DESC"
)
public class Chat extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private Users sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private Users receiver;

    @OneToMany(mappedBy = "chat", fetch = FetchType.EAGER)
    @OrderBy("createdAt DESC")
    private List<Message> messages;

    @Transient
    public String getChatName(Long senderId) {
        if(receiver.getId().equals(senderId)) {
            return sender.getName();
        }
        return receiver.getName();
    }
}
