package com.sinch.SMS_routing_service.domain.message.model;

import com.sinch.SMS_routing_service.domain.carrier.enums.Carrier;
import com.sinch.SMS_routing_service.domain.message.enums.MessageStatus;

public class Message {
    private String id;
    private String destinationNumber;
    private String content;
    private String format;
    private MessageStatus status;



    private Carrier carrier;

    public Message(String id, String destinationNumber, String content, String format, MessageStatus status) {
        this.id = id;
        this.destinationNumber = destinationNumber;
        this.content = content;
        this.format = format;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getDestinationNumber() {
        return destinationNumber;
    }

    public String getContent() {
        return content;
    }

    public String getFormat() {
        return format;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public Carrier getCarrier() {
        return carrier;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDestinationNumber(String destinationNumber) {
        this.destinationNumber = destinationNumber;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }
    public void setCarrier(Carrier carrier) {
        this.carrier = carrier;
    }

}
