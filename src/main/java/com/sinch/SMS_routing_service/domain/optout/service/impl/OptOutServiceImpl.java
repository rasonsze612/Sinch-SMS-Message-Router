package com.sinch.SMS_routing_service.domain.optout.service.impl;

import com.sinch.SMS_routing_service.domain.optout.repo.OptOutRepository;
import com.sinch.SMS_routing_service.domain.optout.service.OptOutService;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class OptOutServiceImpl implements OptOutService {

    private final OptOutRepository optOutRepository;

    public OptOutServiceImpl(OptOutRepository optOutRepository) {
        this.optOutRepository = optOutRepository;
    }

    @Override
    public void add(String phoneNumber) {

        optOutRepository.add(phoneNumber);

    }

    @Override
    public boolean checkNumberIsOptOut(String phoneNumber) {

        boolean isOptOutResult = optOutRepository.contains(phoneNumber);
        return isOptOutResult;

    }
}
