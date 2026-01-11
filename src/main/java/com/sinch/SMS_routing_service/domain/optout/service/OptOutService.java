package com.sinch.SMS_routing_service.domain.optout.service;

public interface OptOutService {
    void add(String phoneNumber);
    boolean checkNumberIsOptOut(String phoneNumber);
}
