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

/**
 * Messages API Controller.
 * Description:
 *   Exposes HTTP endpoints for sending SMS messages, querying message status, and managing opt-out numbers.
 * Author: Rason Sze
 * Date: 2026-01-11
 */
@RestController
public class MessagesController {

    private final MessageService messageService;
    private final OptOutService optOutService;
    private static final Logger log = LoggerFactory.getLogger(MessagesController.class);

    public MessagesController(MessageService messageService, OptOutService optOutService) {
        this.messageService = messageService;
        this.optOutService = optOutService;
    }
    /**
     * API: Send Message
     * Endpoint:
     *   POST /messages
     * Description:
     *   Submits a message for delivery. The service will decide routing and initial status.
     * Request Body:
     *   - destination_number: Destination phone number in simplified E.164 format.
     *   - content: Message content.
     *   - format: Message format. Currently supports SMS only.
     * Response:
     *   - id: Generated message id.
     *   - status: Initial message status (e.g., PENDING or BLOCKED).
     *   - carrier: Resolved carrier (e.g., TELSTRA/OPTUS/SPARK/GLOBAL/OPT_OUT).
     */
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

    /**
     * API: Get Message Status
     * Endpoint:
     *   GET /messages/{id}
     * Description:
     *   Retrieves the latest known status for a given message id.
     * Path Variables:
     *   - id: Message id.
     * Response:
     *   - id: Message id.
     *   - status: Current message status.
     *   - carrier: Carrier assigned to this message.
     */
    @GetMapping("/messages/{id}")
    public MessageStatusResponse getMessageStatus(@PathVariable @NotBlank String id) {
        log.info("GET /messages/{}", id);
        MessageStatusResponse resp = messageService.getStatus(id);
        log.info("GET /messages/{} result status={} carrier={}", id, resp.status(), resp.carrier());
        return resp;
    }

    /**
     * API: Opt-out Phone Number
     * Endpoint:
     *   POST /optout/{phoneNumber}
     * Description:
     *   Adds the given phone number to the opt-out list.
     * Path Variables:
     *   - phoneNumber: Phone number in simplified E.164 format.
     * Response:
     *   - phoneNumber: The number that has been opted out.
     *   - status: Operation result status (e.g., OK).
     */
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
