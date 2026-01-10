package com.sinch.SMS_routing_service.api;

import com.sinch.SMS_routing_service.api.dto.MessageStatusResponse;
import com.sinch.SMS_routing_service.api.dto.OptOutResponse;
import com.sinch.SMS_routing_service.api.dto.SendMessageRequest;
import com.sinch.SMS_routing_service.api.dto.SendMessageResponse;
import org.springframework.web.bind.annotation.*;

@RestController
public class MessagesController {

    @PostMapping("/messages")
    public SendMessageResponse sendMessage(@RequestBody SendMessageRequest request) {

        return new SendMessageResponse("123456","PENDING");
    }

    @GetMapping("/messages/{id}")
    public MessageStatusResponse getMessageStatus(@PathVariable String id) {

        return new MessageStatusResponse("123456","PENDING");
    }

    @PostMapping("/optout/{phoneNumber}")
    public OptOutResponse optOut(@PathVariable String phoneNumber) {

        return new OptOutResponse(phoneNumber, "OK");
    }
}
