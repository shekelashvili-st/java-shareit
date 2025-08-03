package ru.practicum.shareit.exception;

public class UserPermissionsException extends RuntimeException {
    public UserPermissionsException(String msg) {
        super(msg);
    }
}
