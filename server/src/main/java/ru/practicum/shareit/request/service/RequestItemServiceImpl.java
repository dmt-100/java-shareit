package ru.practicum.shareit.request.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.model.RequestItem;
import ru.practicum.shareit.request.repository.RequestItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class RequestItemServiceImpl implements RequestItemService {
    RequestItemRepository requestItemRepository;
    UserRepository userRepository;

    @Transactional
    @Override
    public RequestItem addRequest(long ownerId, RequestItem request) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("No such owner"));
        request.setRequester(owner);
        return requestItemRepository.save(request);
    }

    @Override
    public List<RequestItem> getOwnerRequests(long ownerId) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("No such owner");
        }
        return requestItemRepository.findAllByRequesterIdOrderByCreatedDesc(ownerId);
    }

    @Override
    public List<RequestItem> getAllRequests(long ownerId, int from, int size) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("No such owner");
        }
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        return requestItemRepository.findRequestPages(page, ownerId);
    }

    @Override
    public RequestItem getRequestById(long ownerId, long requestId) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("No such owner");
        }
        return requestItemRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("No such request"));
    }
}
