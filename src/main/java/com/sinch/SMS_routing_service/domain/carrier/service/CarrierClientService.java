package com.sinch.SMS_routing_service.domain.carrier.service;

import com.sinch.SMS_routing_service.domain.carrier.enums.Carrier;
import com.sinch.SMS_routing_service.domain.message.model.Message;

public interface CarrierClientService {
    boolean send(Message message);
    Carrier carrier();
}
