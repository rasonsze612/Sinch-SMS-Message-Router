package com.sinch.SMS_routing_service.domain.optout.repo.impl;

import com.sinch.SMS_routing_service.domain.optout.repo.OptOutRepository;
import com.sinch.SMS_routing_service.exception.BadRequestException;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryOptOutRepositoryImpl implements OptOutRepository {

    private final Set<String> optedOutSet = ConcurrentHashMap.newKeySet();

    @Override
    public void add(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new BadRequestException("phoneNumber is required");
        }
        optedOutSet.add(phoneNumber);
    }

    @Override
    public boolean contains(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()){
            throw new BadRequestException("phoneNumber is required");
        }
        return optedOutSet.contains(phoneNumber);

    }
}
