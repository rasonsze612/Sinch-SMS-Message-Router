package com.sinch.SMS_routing_service.domain.optout.repo;

public interface OptOutRepository {

    void add(String phoneNumber);
    boolean contains(String phoneNumber);
}