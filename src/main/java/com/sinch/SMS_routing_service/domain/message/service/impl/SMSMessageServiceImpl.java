package com.sinch.SMS_routing_service.domain.message.service.impl;

import com.sinch.SMS_routing_service.api.dto.MessageStatusResponse;
import com.sinch.SMS_routing_service.api.dto.SendMessageRequest;
import com.sinch.SMS_routing_service.api.dto.SendMessageResponse;
import com.sinch.SMS_routing_service.domain.carrier.enums.Carrier;
import com.sinch.SMS_routing_service.domain.carrier.service.CarrierClientService;
import com.sinch.SMS_routing_service.domain.carrier.service.CarrierRouterService;
import com.sinch.SMS_routing_service.domain.message.enums.MessageStatus;
import com.sinch.SMS_routing_service.domain.message.model.Message;
import com.sinch.SMS_routing_service.domain.message.repo.MessageRepository;
import com.sinch.SMS_routing_service.domain.message.service.MessageService;
import com.sinch.SMS_routing_service.domain.optout.service.OptOutService;
import com.sinch.SMS_routing_service.exception.BadRequestException;
import com.sinch.SMS_routing_service.exception.NotFoundException;
import com.sinch.SMS_routing_service.exception.SendMessageException;
import org.apache.logging.log4j.util.InternalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Service
public class SMSMessageServiceImpl implements MessageService {

    private final MessageRepository messageRepo;

    private final OptOutService optOutService;
    /** Router service that selects the carrier and provides the corresponding client implementation. */
    private final CarrierRouterService carrierRouter;
    private static final Logger log = LoggerFactory.getLogger(SMSMessageServiceImpl.class);

    public SMSMessageServiceImpl(MessageRepository messageRepo, OptOutService optOutService, CarrierRouterService carrierRouter) {
        this.messageRepo = messageRepo;
        this.optOutService = optOutService;
        this.carrierRouter = carrierRouter;
    }

    @Override
    public SendMessageResponse send(SendMessageRequest req) {
        // message id generator,
        // but it should be a distributed unique ID if in microservice env, like Snowflake ID
        String id = UUID.randomUUID().toString();
        log.info("message send service start id={} dest={} format={} contentLen={}",
                id, req.destination_number(), req.format(),
                req.content().length());

        if (req.destination_number() == null || req.destination_number().isBlank())
            throw new BadRequestException("destination_number is required");
        if (req.content().isBlank())
            throw new BadRequestException("content is required");
        if (req.format() == null || req.format().isBlank())
            throw new BadRequestException("format is required");
        // SMS is the only acceptable value for this message type
        if (!"SMS".equalsIgnoreCase(req.format()))
            throw new BadRequestException("unsupported format: " + req.format());

        // check if the number is OptOut
        boolean isOptOut = optOutService.checkNumberIsOptOut(req.destination_number());
        // It will be set to Block status if it is OptOut number
        // The message status is initialized to Pending when the message is created.
        MessageStatus status = isOptOut ? MessageStatus.BLOCKED : MessageStatus.PENDING;

        // It will be set to OPT_OUT for message's carrier if number is OptOut
        // Otherwise check the route to get the correct carrier by the phone number prefix
        Carrier carrier = isOptOut ? Carrier.OPT_OUT :
                carrierRouter.route(req.destination_number());
        Message msg = new Message(
                id,
                req.destination_number(),
                req.content(),
                req.format(),
                status,
                carrier
        );

        log.info("message start to save id={} dest={} status={} carrier={}",
                msg.getId(),
                msg.getDestinationNumber(),
                msg.getStatus(),
                msg.getCarrier());

        // the message status would be Block or Pending
        Message saveResult = messageRepo.save(msg);
        if(saveResult == null) {
            log.error("message save fail id={} dest={}", msg.getId(), msg.getDestinationNumber());
            throw new SendMessageException("Message Save Fail");
        }

        log.info("message saved id={} dest={} status={} carrier={}",
                msg.getId(),
                msg.getDestinationNumber(),
                msg.getStatus(),
                msg.getCarrier());

        // send to carrier if status is not block
        if (MessageStatus.BLOCKED != status){
            log.info("message start to send SMS via carrier id={} dest={} carrier={}", msg.getId(), msg.getDestinationNumber(), msg.getCarrier());

            // Get the correct carrier(Telstra/Optus, Spark, Global) client by the incoming message number
            CarrierClientService carrierClientService = carrierRouter.getCarrierClientService(carrier);
            boolean sendResult = carrierClientService.send(msg);
            if (sendResult){
                msg.setStatus(MessageStatus.SENT);
                messageRepo.updateStatus(msg.getId(), MessageStatus.SENT);
                log.info("message update to sent status id={} dest={} carrier={}", msg.getId(), msg.getDestinationNumber(), msg.getCarrier());
                // Simulate a delivery receipt update
                // (in real systems, DELIVERED is typically set via carrier callback/webhook or polling).
                DelayUpdateToDelivered(msg);
            }else {
                log.error("message sent fail via carrier id={} dest={} carrier={}", msg.getId(), msg.getDestinationNumber(), msg.getCarrier());
            }
        }

        return new SendMessageResponse(id, msg.getStatus(), carrier);
    }

    @Override
    public MessageStatusResponse getStatus(String id) {
        Message msg = messageRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("message not found: " + id));
        return new MessageStatusResponse(msg.getId(), msg.getStatus(), msg.getCarrier());
    }

    private void DelayUpdateToDelivered(Message msg) {
        log.info("message start to change status to DELIVERED id={} dest={} carrier={}",
                msg.getId(), msg.getDestinationNumber(), msg.getCarrier());

        Executor delayed = CompletableFuture.delayedExecutor(2000, TimeUnit.MILLISECONDS);
        CompletableFuture.runAsync(() -> messageRepo.updateStatus(msg.getId(),MessageStatus.DELIVERED), delayed);
    }

}
