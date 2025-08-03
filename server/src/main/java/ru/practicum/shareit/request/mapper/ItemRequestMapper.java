package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper {
    ItemRequestDto modelToDto(ItemRequest itemRequest);

    List<ItemRequestDto> modelToDto(List<ItemRequest> itemRequests);

    ItemRequest createDtoToModel(CreateItemRequestDto dto);
}
