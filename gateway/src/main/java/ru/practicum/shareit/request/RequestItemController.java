package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestItemDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.util.Headers.USER_ID;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
@Slf4j
public class RequestItemController {
    RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> addRequest(@RequestHeader(USER_ID) @NotNull Long ownerId,
                                             @Valid @RequestBody RequestItemDto request) {
        log.info("Add request, userId = {}, request = {}", ownerId, request);
        return requestClient.addRequest(ownerId, request);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnerRequests(@RequestHeader(USER_ID) @NotNull Long ownerId) {
        log.info("Get owner requests, ownerId ={}", ownerId);
        return requestClient.getOwnerRequests(ownerId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(USER_ID) @NotNull Long ownerId,
                                                 @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                 @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Get all requests, ownerId = {}, from = {}, size = {}", ownerId, from, size);
        return requestClient.getAllRequests(ownerId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader(USER_ID) @NotNull Long ownerId,
                                                 @PathVariable long requestId) {
        log.info("Get request by id, userId = {}, requestId = {}", ownerId, requestId);
        return requestClient.getRequestById(ownerId, requestId);
    }
}