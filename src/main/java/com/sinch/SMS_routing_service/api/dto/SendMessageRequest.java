package com.sinch.SMS_routing_service.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SendMessageRequest(
        @NotBlank(message = "destination_number is required")
        @Pattern(regexp = "^\\+[1-9]\\d{7,14}$", message = "destination_number must be E.164, e.g. +61491570156")
        String destination_number,
        @NotBlank(message = "content is required")
        @Size(max = 120, message = "content is too long")
        String content,
        @NotBlank(message = "format is required")
        @Pattern(regexp = "SMS", message = "format must be SMS")
        String format
) {}
