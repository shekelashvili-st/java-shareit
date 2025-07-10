package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;

import java.sql.Timestamp;

@Data
public class CommentDto {
    private long id;

    private String authorName;

    private Item item;

    private String text;

    private Timestamp created;
}
