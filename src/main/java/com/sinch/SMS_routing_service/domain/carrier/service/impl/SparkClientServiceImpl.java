package com.sinch.SMS_routing_service.domain.carrier.service.impl;

import com.sinch.SMS_routing_service.domain.carrier.enums.Carrier;
import com.sinch.SMS_routing_service.domain.carrier.service.CarrierClientService;
import com.sinch.SMS_routing_service.domain.message.model.Message;

public class SparkClientServiceImpl implements CarrierClientService {
    @Override
    public boolean send(Message message) {
        return true;
    }

    @Override
    public Carrier carrier() {
        return Carrier.SPARK;
    }
}
