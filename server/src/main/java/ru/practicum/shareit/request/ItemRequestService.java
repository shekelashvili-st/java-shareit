package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestService {
    private final ItemRequestRepository repository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestMapper mapper;

    @Transactional
    public ItemRequestDto create(CreateItemRequestDto itemRequest, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IdNotFoundException("User with id" + userId + " not found!"));
        ItemRequest newItemRequest = mapper.createDtoToModel(itemRequest);
        newItemRequest.setRequester(user);
        ItemRequest itemRequestFromDb = repository.save(newItemRequest);
        return mapper.modelToDto(itemRequestFromDb);
    }

    public List<ItemRequestDto> findAllByRequesterId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IdNotFoundException("User with id" + userId + " not found!"));

        Map<Long, ItemRequest> itemRequestsMap = repository.findAllByRequesterIdOrderByCreatedDesc(userId)
                .stream()
                .collect(Collectors.toMap(ItemRequest::getId, Function.identity()));
        Map<Long, List<Item>> itemMap = itemRepository.findAllByItemRequestIdIn(itemRequestsMap.keySet())
                .stream()
                .collect(Collectors.groupingBy(item -> item.getItemRequest().getId()));

        List<ItemRequestDto> dtos = new ArrayList<>();

        for (Long id : itemRequestsMap.keySet()) {
            ItemRequestDto dto = mapper.modelToDto(itemRequestsMap.get(id));
            dto.setItems(itemMap.get(id));
            dtos.add(dto);
        }

        return dtos;
    }

    public List<ItemRequestDto> findAll(Long userId) {
        return mapper.modelToDto(repository.findAllByRequesterIdNotOrderByCreatedDesc(userId));
    }

    public ItemRequestDto findById(Long requestId) {
        ItemRequest request = repository.findById(requestId)
                .orElseThrow(() -> new IdNotFoundException("Request with id" + requestId + " not found!"));
        List<Item> items = itemRepository.findAllByItemRequestId(requestId);
        ItemRequestDto dto = mapper.modelToDto(request);
        dto.setItems(items);
        return dto;
    }
}
