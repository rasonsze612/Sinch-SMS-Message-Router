package com.sinch.SMS_routing_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sinch.SMS_routing_service.api.dto.SendMessageRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest
@AutoConfigureMockMvc
public class MessageControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    /**
     * Test Case:
     * - Send to valid AU number → routes to Telstra/Optus
     */
    @Test
    void sendToValidAuNumber_shouldRouteToTelstraOrOptus() throws Exception {
        // use a dedicated AU number to avoid test order side effects
        var req = new SendMessageRequest(
                "+61491570001",
                "Hello AU",
                "SMS");

        mockMvc.perform(post("/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", not(blankOrNullString())))
                .andExpect(jsonPath("$.status", is("SENT")))
                .andExpect(jsonPath("$.carrier", anyOf(is("TELSTRA"), is("OPTUS"))));
    }

    /**
     * Test Case:
     * - Send to opted-out number → blocked (check via status endpoint)
     */
    @Test
    void sendToOptedOutNumber_shouldBeBlocked_andStatusEndpointShouldReturnBlocked() throws Exception {
        String phone = "+61491570002";

        // 1) opt-out first
        mockMvc.perform(post("/optout/{phoneNumber}", phone))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phoneNumber", is(phone)))
                .andExpect(jsonPath("$.status", is("OK")));

        // 2) send message to opted-out number
        var req = new SendMessageRequest(phone, "Hello OptOut", "SMS");

        String responseJson = mockMvc.perform(post("/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", not(blankOrNullString())))
                .andExpect(jsonPath("$.status", is("BLOCKED")))
                .andExpect(jsonPath("$.carrier", is("OPT_OUT")))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract message id from response JSON
        String messageId = objectMapper.readTree(responseJson).get("id").asText();

        // 3) check status via GET /messages/{id}
        mockMvc.perform(get("/messages/{id}", messageId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(messageId)))
                .andExpect(jsonPath("$.status", is("BLOCKED")))
                .andExpect(jsonPath("$.carrier", is("OPT_OUT")));
    }

    /**
     * Test Case:
     * - Send to NZ number → routes to Spark
     */
    @Test
    void sendToNzNumber_shouldRouteToSpark() throws Exception {
        var req = new SendMessageRequest("+64211234567", "Hello NZ", "SMS");

        mockMvc.perform(post("/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", not(blankOrNullString())))
                .andExpect(jsonPath("$.status", is("SENT")))
                .andExpect(jsonPath("$.carrier", is("SPARK")));
    }
}
