package ru.yandex.practicum.filmorate.controller.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.exceptions.UserDoesNotExistException;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

@Component
@Slf4j
public class UserValidator {
    private final InMemoryUserStorage userStorage;

    @Autowired
    public UserValidator(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void validateUserExists(@PathVariable Integer id) {
        if (!userStorage.getUsers().containsKey(id)) {
            String message = "Пользователя c таким ID не существует.";
            log.error(message);
            throw new UserDoesNotExistException(message);
        }
    }
}
