package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.RequestItemDto;
import ru.practicum.shareit.request.dto.RequestItemResponseDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.RequestItem;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static ru.practicum.shareit.request.RequestItemRepositoryTest.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestMapperTest {
    RequestItem requestItem;
    RequestItemDto requestItemDto;
    RequestItemResponseDto responseDto;

    @Test
    void dtoToRequestItem() {
        requestItemDto = RequestItemDto.builder()
                .description("text")
                .created(LocalDateTime.now())
                .build();
        requestItem = RequestMapper.dtoToRequestItem(requestItemDto);
        assertNotNull(requestItem);
        assertThat(requestItem.getDescription(), equalTo(requestItemDto.getDescription()));
    }

    @Test
    void requestItemToResponseDto() {
        requestItem = RequestItem.builder()
                .id(1L)
                .requester(createRequester())
                .items(null)
                .description("text")
                .created(LocalDateTime.now())
                .build();
        responseDto = RequestMapper.requestItemToResponseDto(requestItem);
        assertNotNull(responseDto);
        assertThat(requestItem.getId(), equalTo(responseDto.getId()));
    }
}
