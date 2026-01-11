package com.sinch.SMS_routing_service.domain.message.repo;

import com.sinch.SMS_routing_service.domain.message.enums.MessageStatus;
import com.sinch.SMS_routing_service.domain.message.model.Message;

import java.util.Optional;

public interface MessageRepository {

    Message save(Message message);
    Optional<Message> findById(String id);
    void updateStatus(String messageId, MessageStatus newStatus);
}
