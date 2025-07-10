package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

@Data
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private boolean available;
    private List<Comment> comments;
}
