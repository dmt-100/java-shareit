package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.model.RequestItem;

import java.util.List;

public interface RequestItemService {
    RequestItem addRequest(long ownerId, RequestItem request);

    List<RequestItem> getOwnerRequests(long ownerId);

    List<RequestItem> getAllRequests(long ownerId, int from, int size);

    RequestItem getRequestById(long ownerId, long requestId);
}
