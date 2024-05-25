package com.example.messageserver.Repository;

import com.example.messageserver.Entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<MessageEntity,Long> {
    Long countByReceiverIdAndReadStatus(Long receiverId, boolean readStatus);
    List<MessageEntity> findAllByReceiverIdOrderBySendTimeDesc(Long receiverId);
}
