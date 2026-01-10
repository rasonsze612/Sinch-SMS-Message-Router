package com.sinch.SMS_routing_service.api.dto;

public record SendMessageRequest(
        String destination_number,
        String content,
        String format
) {}
