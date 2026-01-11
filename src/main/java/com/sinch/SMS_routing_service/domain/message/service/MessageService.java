package com.sinch.SMS_routing_service.domain.message.service;

import com.sinch.SMS_routing_service.api.dto.MessageStatusResponse;
import com.sinch.SMS_routing_service.api.dto.SendMessageRequest;
import com.sinch.SMS_routing_service.api.dto.SendMessageResponse;

public interface MessageService {
    SendMessageResponse send(SendMessageRequest req);
    MessageStatusResponse getStatus(String id);
}
