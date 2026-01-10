package com.sinch.SMS_routing_service.api.dto;

public record OptOutResponse(
        String phoneNumber,
        String status
) {}