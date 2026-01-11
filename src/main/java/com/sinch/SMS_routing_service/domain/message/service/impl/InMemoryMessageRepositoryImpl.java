package com.sinch.SMS_routing_service.domain.message.service.impl;

import com.sinch.SMS_routing_service.domain.message.enums.MessageStatus;
import com.sinch.SMS_routing_service.domain.message.model.Message;
import com.sinch.SMS_routing_service.domain.message.repo.MessageRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryMessageRepositoryImpl implements MessageRepository {

    private final ConcurrentHashMap<String, Message> store = new ConcurrentHashMap<>();

    @Override
    public Message save(Message message) {

        store.put(message.getId(), message);

        return message;
    }

    @Override
    public Optional<Message> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public void updateStatus(String id, MessageStatus newStatus) {

    }
}