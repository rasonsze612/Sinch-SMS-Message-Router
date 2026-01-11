package com.sinch.SMS_routing_service.api;

import com.sinch.SMS_routing_service.api.dto.MessageStatusResponse;
import com.sinch.SMS_routing_service.api.dto.OptOutResponse;
import com.sinch.SMS_routing_service.api.dto.SendMessageRequest;
import com.sinch.SMS_routing_service.api.dto.SendMessageResponse;
import com.sinch.SMS_routing_service.domain.message.enums.MessageStatus;
import com.sinch.SMS_routing_service.domain.message.service.MessageService;
import com.sinch.SMS_routing_service.domain.optout.service.OptOutService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
public class MessagesController {

    private final MessageService messageService;
    private final OptOutService optOutService;
    private static final Logger log = LoggerFactory.getLogger(MessagesController.class);

    public MessagesController(MessageService messageService, OptOutService optOutService) {
        this.messageService = messageService;
        this.optOutService = optOutService;
    }

    @PostMapping("/messages")
    public SendMessageResponse sendMessage(@Valid @RequestBody SendMessageRequest request) {
        log.info("POST /messages dest={} format={} contentLen={}",
                request.destination_number(),
                request.format(),
                request.content().length());

        SendMessageResponse resp = messageService.send(request);

        log.info("POST /messages result id={} status={} carrier={}", resp.id(), resp.status(), resp.carrier());
        return resp;
    }

    @GetMapping("/messages/{id}")
    public MessageStatusResponse getMessageStatus(@PathVariable @NotBlank String id) {
        log.info("GET /messages/{}", id);

        MessageStatusResponse resp = messageService.getStatus(id);

        log.info("GET /messages/{} result status={} carrier={}", id, resp.status(), resp.carrier());
        return resp;
    }

    @PostMapping("/optout/{phoneNumber}")
    public OptOutResponse optOut(@PathVariable
                                 @NotBlank(message="phoneNumber is required")
                                 @Pattern(regexp="^\\+[1-9]\\d{7,14}$", message="phoneNumber must be E.164")
                                 String phoneNumber) {
        log.info("POST /optout/{}", phoneNumber);
        optOutService.add(phoneNumber);
        return new OptOutResponse(phoneNumber, "OK");
    }
}
