# SMS Routing Service

A lightweight Spring Boot service that accepts SMS send requests, routes them to a simulated carrier, and exposes
endpoints to query message status and manage opt-out numbers.

## Tech Stack
Frameworks:
- Spring Boot 3.5.9
- Spring Web (spring-boot-starter-web)
- Spring Validation (spring-boot-starter-validation)

Test Dependencies:
- Spring Boot Test (spring-boot-starter-test)

Build:
- Maven (via Maven Wrapper)

## Getting Started
Prerequisites:
- Java 17
- Maven wrapper included (`./mvnw`)

Run locally:
```bash
./mvnw spring-boot:run
```

Run tests:
```bash
./mvnw test
```

## Assumptions
- This is a take-home demo and uses in-memory repositories; data is lost on restart.
- Only `SMS` is supported and validated on input.
- Phone numbers must be in simplified E.164 format (e.g. `+61491570156`).
- Carrier routing rules:
    - `+61` (AU) alternates between TELSTRA and OPTUS.
    - `+64` (NZ) routes to SPARK.
    - All other prefixes route to GLOBAL.
- Carrier clients are stubs that always return success.
- Message status lifecycle:
    - `PENDING` on creation, then immediately `SENT` after a successful carrier call.
    - `DELIVERED` is simulated after a short delay (2 seconds).
    - `BLOCKED` if the destination is on the opt-out list, with carrier set to `OPT_OUT`.
- No authentication, rate limiting, or persistence is implemented.

## API
Base URL: `http://localhost:8080`

### POST /messages
Send a new SMS message.

Request body:
```json
{
  "destination_number": "+61491570001",
  "content": "Hello AU",
  "format": "SMS"
}
```

Response:
```json
{
  "id": "b1ad8cf8-7c4f-4b80-9a90-92a0c7f9f2b1",
  "status": "SENT",
  "carrier": "TELSTRA"
}
```

Validation:
- `destination_number`: E.164, `+` plus 8-15 digits.
- `content`: non-empty, max 1600 chars.
- `format`: must be `SMS`.

Example:
```bash
curl -X POST http://localhost:8080/messages \
  -H "Content-Type: application/json" \
  -d '{"destination_number":"+61491570001","content":"Hello AU","format":"SMS"}'
```

### GET /messages/{id}
Query the latest known status for a message.

Response:
```json
{
  "id": "b1ad8cf8-7c4f-4b80-9a90-92a0c7f9f2b1",
  "status": "DELIVERED",
  "carrier": "TELSTRA"
}
```

Example:
```bash
curl http://localhost:8080/messages/b1ad8cf8-7c4f-4b80-9a90-92a0c7f9f2b1
```

### POST /optout/{phoneNumber}
Add a phone number to the opt-out list.

Response:
```json
{
  "phoneNumber": "+61491570002",
  "status": "OK"
}
```

Example:
```bash
curl -X POST http://localhost:8080/optout/+61491570002
```

### Error format
Errors use a simple JSON body:
```json
{
  "code": "bad_request",
  "message": "destination_number must be E.164, e.g. +61491570156"
}
```


## Unit Tests
- `MessageControllerTest`: validates routing rules (AU/NZ), opt-out blocking, and status lookup.
- `SmsRoutingServiceDemoApplicationTests`: verifies Spring context loads.

## Directory Structure
```
src/main/java/com/sinch/smsroutingservicedemo
  api/
    MessagesController.java
    dto/
      MessageStatusResponse.java
      OptOutResponse.java
      SendMessageRequest.java
      SendMessageResponse.java
  domain/
    carrier/
      enums/Carrier.java
      service/
        CarrierClientService.java
        CarrierRouterService.java
        impl/
          CarrierRouterServiceImpl.java
          GlobalClientServiceImpl.java
          OptusClientServiceImpl.java
          SparkClientServiceImpl.java
          TelstraClientServiceImpl.java
    message/
      enums/MessageStatus.java
      model/Message.java
      repo/
        MessageRepository.java
        impl/InMemoryMessageRepositoryImpl.java
      service/
        MessageService.java
        impl/SMSMessageServiceImpl.java
    optout/
      repo/
        OptOutRepository.java
        impl/InMemoryOptOutRepositoryImpl.java
      service/
        OptOutService.java
        impl/OptOutServiceImpl.java
  exception/
    ApiExceptionHandler.java
    BadRequestException.java
    NotFoundException.java
    SendMessageException.java
  SmsRoutingServiceDemoApplication.java
src/main/resources
  application.properties
src/test/java/com/sinch/smsroutingservicedemo
  MessageControllerTest.java
  SmsRoutingServiceDemoApplicationTests.java
```

## Future Improvements
- Add API authentication and authorization (API keys or OAuth2).
- Use a message queue for async dispatch to carriers instead of direct API calls.
- Add message priority and schedule dispatch based on priority.
- Retrieve final SMS status via carrier callbacks/webhooks, or use polling.
- Add failure reason and reason code fields to message status.
- Maintain carrier-specific reason code mapping table.
- Add an opt-out release API (remove number from opt-out list).
- Capture opt-out reasons in the opt-out API.
- Replace in-memory storage with a durable data store.
- Add metrics, tracing, and structured audit logs for compliance.
- Message ID should be distributed unique ID if in microservice env, like Snowflake ID

## AI Assistance
This project was built with AI assistance in the following areas:
- Regular expression for phone number validation.
- Writing parts of the unit tests.<br>
  （AI mainly helped me draft unit tests, and I still validated and reviewed every generated test before merging）
- Overall code review for consistency and potential issues.
