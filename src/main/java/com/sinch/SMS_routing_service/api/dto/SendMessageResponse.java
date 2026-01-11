package com.sinch.SMS_routing_service.api.dto;

import com.sinch.SMS_routing_service.domain.carrier.enums.Carrier;
import com.sinch.SMS_routing_service.domain.message.enums.MessageStatus;

public record SendMessageResponse(
        String id,
        MessageStatus status,
        Carrier carrier
) {}