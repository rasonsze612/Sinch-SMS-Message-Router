package com.sinch.SMS_routing_service.domain.carrier.service;

import com.sinch.SMS_routing_service.domain.carrier.enums.Carrier;

public interface CarrierRouterService {
    Carrier route(String destinationNumber);
    CarrierClientService getCarrierClientService(Carrier carrier);
}
