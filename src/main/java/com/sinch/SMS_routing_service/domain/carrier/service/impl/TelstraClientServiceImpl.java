package com.sinch.SMS_routing_service.domain.carrier.service.impl;

import com.sinch.SMS_routing_service.domain.carrier.enums.Carrier;
import com.sinch.SMS_routing_service.domain.carrier.service.CarrierClientService;
import com.sinch.SMS_routing_service.domain.message.model.Message;

public class TelstraClientServiceImpl implements CarrierClientService {
    @Override
    public boolean send(Message message) {
        return false;
    }

    @Override
    public Carrier carrier() {
        return Carrier.TELSTRA;
    }
}
