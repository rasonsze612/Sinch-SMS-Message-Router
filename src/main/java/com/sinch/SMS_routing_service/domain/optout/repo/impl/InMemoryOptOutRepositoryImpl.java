package com.sinch.SMS_routing_service.domain.optout.repo.impl;

import com.sinch.SMS_routing_service.domain.optout.repo.OptOutRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryOptOutRepositoryImpl implements OptOutRepository {

    private final Set<String> optedOutSet = ConcurrentHashMap.newKeySet();

    @Override
    public void add(String phoneNumber) {
        optedOutSet.add(phoneNumber);
    }

    @Override
    public boolean contains(String phoneNumber) {
        return optedOutSet.contains(phoneNumber);

    }
}
