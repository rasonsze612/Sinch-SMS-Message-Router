package com.sinch.SMS_routing_service.domain.optout.service.impl;

import com.sinch.SMS_routing_service.domain.optout.service.OptOutService;
import org.springframework.stereotype.Service;

@Service
public class OptOutServiceImpl implements OptOutService {


    @Override
    public void add(String phoneNumber) {

    }

    @Override
    public boolean checkNumberIsOptOut(String phoneNumber) {

        return false;
    }
}
