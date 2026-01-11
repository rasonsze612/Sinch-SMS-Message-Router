package com.sinch.SMS_routing_service.api;

import com.sinch.SMS_routing_service.api.dto.MessageStatusResponse;
import com.sinch.SMS_routing_service.api.dto.OptOutResponse;
import com.sinch.SMS_routing_service.api.dto.SendMessageRequest;
import com.sinch.SMS_routing_service.api.dto.SendMessageResponse;
import com.sinch.SMS_routing_service.domain.message.enums.MessageStatus;
import com.sinch.SMS_routing_service.domain.message.service.MessageService;
import com.sinch.SMS_routing_service.domain.optout.service.OptOutService;
import org.springframework.web.bind.annotation.*;

@RestController
public class MessagesController {

    private final MessageService messageService;
    private final OptOutService optOutService;

    public MessagesController(MessageService messageService, OptOutService optOutService) {
        this.messageService = messageService;
        this.optOutService = optOutService;
    }

    @PostMapping("/messages")
    public SendMessageResponse sendMessage(@RequestBody SendMessageRequest request) {

        SendMessageResponse resp = messageService.send(request);
        return resp;
    }

    @GetMapping("/messages/{id}")
    public MessageStatusResponse getMessageStatus(@PathVariable String id) {

        MessageStatusResponse resp = messageService.getStatus(id);

        return resp;
    }

    @PostMapping("/optout/{phoneNumber}")
    public OptOutResponse optOut(@PathVariable String phoneNumber) {

        return new OptOutResponse(phoneNumber, "OK");
    }
}
