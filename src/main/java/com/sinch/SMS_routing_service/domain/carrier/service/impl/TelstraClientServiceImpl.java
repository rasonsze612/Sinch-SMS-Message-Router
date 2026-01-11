package com.sinch.SMS_routing_service.domain.carrier.service.impl;

import com.sinch.SMS_routing_service.domain.carrier.enums.Carrier;
import com.sinch.SMS_routing_service.domain.carrier.service.CarrierClientService;
import com.sinch.SMS_routing_service.domain.message.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TelstraClientServiceImpl implements CarrierClientService {

    private static final Logger log = LoggerFactory.getLogger(TelstraClientServiceImpl.class);

    @Override
    public boolean send(Message message) {
        // Real implementation:
        // Build HTTP request to Telstra SMS API endpoint
        // Check API RESP status code is 202, then return true, else false
        log.info("Telstra carrier is sending message id={} number={}", message.getId(), message.getDestinationNumber());
        return true;
    }

    @Override
    public Carrier carrier() {
        return Carrier.TELSTRA;
    }
}
