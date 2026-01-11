package com.sinch.SMS_routing_service.domain.message.service.impl;

import com.sinch.SMS_routing_service.api.dto.MessageStatusResponse;
import com.sinch.SMS_routing_service.api.dto.SendMessageRequest;
import com.sinch.SMS_routing_service.api.dto.SendMessageResponse;
import com.sinch.SMS_routing_service.domain.carrier.enums.Carrier;
import com.sinch.SMS_routing_service.domain.carrier.service.CarrierRouterService;
import com.sinch.SMS_routing_service.domain.message.enums.MessageStatus;
import com.sinch.SMS_routing_service.domain.message.model.Message;
import com.sinch.SMS_routing_service.domain.message.repo.MessageRepository;
import com.sinch.SMS_routing_service.domain.message.service.MessageService;
import com.sinch.SMS_routing_service.domain.optout.service.OptOutService;
import com.sinch.SMS_routing_service.exception.NotFoundException;
import com.sinch.SMS_routing_service.exception.SendMessageException;
import org.apache.logging.log4j.util.InternalException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SMSMessageServiceImpl implements MessageService {

    private final MessageRepository messageRepo;

    private final OptOutService optOutService;
    private final CarrierRouterService carrierRouter;


    public SMSMessageServiceImpl(MessageRepository messageRepo, OptOutService optOutService, CarrierRouterService carrierRouter) {
        this.messageRepo = messageRepo;
        this.optOutService = optOutService;
        this.carrierRouter = carrierRouter;
    }

    @Override
    public SendMessageResponse send(SendMessageRequest req) {
        String id = UUID.randomUUID().toString();

        boolean isOptOut = optOutService.checkNumberIsOptOut(req.destination_number());
        MessageStatus status = isOptOut ? MessageStatus.BLOCKED : MessageStatus.PENDING;
        Carrier carrier = isOptOut ? Carrier.OPT_OUT :
                carrierRouter.route(req.destination_number());
        Message msg = new Message(
                id,
                req.destination_number(),
                req.content(),
                req.format(),
                status
        );

        Message saveResult = messageRepo.save(msg);
        if(saveResult == null) {
            throw new SendMessageException("Message Save Fail");
        }

        return new SendMessageResponse(id, msg.getStatus(), carrier);
    }

    @Override
    public MessageStatusResponse getStatus(String id) {
        Message msg = messageRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("message not found: " + id));
        return new MessageStatusResponse(msg.getId(), msg.getStatus(), msg.getCarrier());
    }

}
