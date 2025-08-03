package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.sql.Timestamp;
import java.util.List;

@Data
public class ItemRequestDto {
    private Long id;

    private String description;

    private User requester;

    private Timestamp created;

    private List<Item> items;
}
