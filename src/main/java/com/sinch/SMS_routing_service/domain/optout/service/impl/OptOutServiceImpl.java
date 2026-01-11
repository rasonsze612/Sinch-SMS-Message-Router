package com.sinch.SMS_routing_service.domain.optout.service.impl;

import com.sinch.SMS_routing_service.domain.optout.repo.OptOutRepository;
import com.sinch.SMS_routing_service.domain.optout.service.OptOutService;
import com.sinch.SMS_routing_service.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class OptOutServiceImpl implements OptOutService {

    private final OptOutRepository optOutRepository;
    private static final Logger log = LoggerFactory.getLogger(OptOutServiceImpl.class);

    public OptOutServiceImpl(OptOutRepository optOutRepository) {
        this.optOutRepository = optOutRepository;
    }

    @Override
    public void add(String phoneNumber) {
        log.info("add OptOut number phoneNumber={}", phoneNumber);
        if (phoneNumber == null || phoneNumber.isBlank())
            throw new BadRequestException("phoneNumber is required");
        optOutRepository.add(phoneNumber);
    }

    @Override
    public boolean checkNumberIsOptOut(String phoneNumber) {
        log.info("checkNumberIsOptOut phoneNumber={}", phoneNumber);
        if (phoneNumber == null || phoneNumber.isBlank())
            throw new BadRequestException("phoneNumber is required");
        boolean isOptOutResult = optOutRepository.contains(phoneNumber);
        log.info("checkNumberIsOptOut Result phoneNumber={}, is OptOut={}", phoneNumber, isOptOutResult);
        return isOptOutResult;
    }
}
