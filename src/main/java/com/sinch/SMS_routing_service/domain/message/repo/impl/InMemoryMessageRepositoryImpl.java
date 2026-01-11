package com.sinch.SMS_routing_service.domain.message.repo.impl;

import com.sinch.SMS_routing_service.domain.message.enums.MessageStatus;
import com.sinch.SMS_routing_service.domain.message.model.Message;
import com.sinch.SMS_routing_service.domain.message.repo.MessageRepository;
import com.sinch.SMS_routing_service.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryMessageRepositoryImpl implements MessageRepository {

    private final ConcurrentHashMap<String, Message> store = new ConcurrentHashMap<>();
    private static final Logger log = LoggerFactory.getLogger(InMemoryMessageRepositoryImpl.class);

    @Override
    public Message save(Message message) {
        if (message == null || message.getId() == null || message.getId().isBlank())
            throw new BadRequestException("message/id is required");
        store.put(message.getId(), message);
        log.info("Message saved for messageId={}", message.getId());
        return message;
    }

    @Override
    public Optional<Message> findById(String id) {
        if (id == null || id.isBlank()) {
            log.error("Find message Id is null/blank id={}", id);
            throw new BadRequestException("message id is required");
        }
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public void updateStatus(String messageId, MessageStatus newStatus) {
        if (messageId == null || messageId.isBlank()) throw new BadRequestException("message id is required");
        if (newStatus == null) throw new BadRequestException("newStatus is required");
        store.computeIfPresent(messageId, (k, msg) -> {
            msg.setStatus(newStatus);
            log.info("Message changed status messageId={} to={}", msg.getId(),newStatus);
            return msg;
        });
    }
}