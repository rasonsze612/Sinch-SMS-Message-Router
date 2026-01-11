package com.sinch.SMS_routing_service.domain.carrier.service.impl;

import com.sinch.SMS_routing_service.domain.carrier.enums.Carrier;
import com.sinch.SMS_routing_service.domain.carrier.service.CarrierClientService;
import com.sinch.SMS_routing_service.domain.carrier.service.CarrierRouterService;
import com.sinch.SMS_routing_service.exception.BadRequestException;
import org.apache.logging.log4j.util.InternalException;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class CarrierRouterServiceImpl implements CarrierRouterService {

    private final AtomicBoolean telstraNext = new AtomicBoolean(true);

    private final Map<Carrier, CarrierClientService> clients = new EnumMap<>(Carrier.class);

    // Injects all CarrierClientService from IOC
    public CarrierRouterServiceImpl(List<CarrierClientService> clientList) {
        for (CarrierClientService c : clientList) {
            clients.put(c.carrier(), c);
        }
    }

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

    @Override
    public CarrierClientService getCarrierClientService(Carrier carrier) {
        CarrierClientService client = clients.get(carrier);
        if (client == null) {
            throw new InternalException("No CarrierClient implementation for carrier: " + carrier);
        }
        return client;
    }

    private Carrier routeAuAlternate() {
        boolean useTelstra = telstraNext.getAndSet(!telstraNext.get());
        return useTelstra ? Carrier.TELSTRA : Carrier.OPTUS;
    }
}
