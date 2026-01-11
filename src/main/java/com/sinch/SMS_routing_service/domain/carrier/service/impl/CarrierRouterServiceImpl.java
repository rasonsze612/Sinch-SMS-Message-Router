package com.sinch.SMS_routing_service.domain.carrier.service.impl;

import com.sinch.SMS_routing_service.domain.carrier.enums.Carrier;
import com.sinch.SMS_routing_service.domain.carrier.service.CarrierClientService;
import com.sinch.SMS_routing_service.domain.carrier.service.CarrierRouterService;
import com.sinch.SMS_routing_service.exception.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class CarrierRouterServiceImpl implements CarrierRouterService {

    private final AtomicBoolean telstraNext = new AtomicBoolean(true);


    @Override
    public Carrier route(String destinationNumber) {
        if (destinationNumber == null || destinationNumber.isBlank()) {
            throw new BadRequestException("destination_number is invalid");
        }

        // return TELSTRA or OPTUS for AU number
        if (destinationNumber.startsWith("+61")) {
            return routeAuAlternate();
        }
        // For NZ number
        if (destinationNumber.startsWith("+64")) {
            return Carrier.SPARK;
        }
        //other numbers
        return Carrier.GLOBAL;
    }

    private Carrier routeAuAlternate() {
        boolean useTelstra = telstraNext.getAndSet(!telstraNext.get());
        return useTelstra ? Carrier.TELSTRA : Carrier.OPTUS;
    }
}
