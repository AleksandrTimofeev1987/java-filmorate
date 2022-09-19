package ru.yandex.practicum.filmorate.controller;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.IncorrectPathVariableException;
import ru.yandex.practicum.filmorate.exceptions.UserDoesNotExistException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import javax.validation.Valid;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("/users")
@Data

public class UserController {

    private final InMemoryUserStorage userStorage;
    private final UserService userService;

    @Autowired
    public UserController(InMemoryUserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    // Получить список всех пользователей
    @GetMapping
    public List<User> getAll() {
        return userStorage.getAll();
    }

    // Добавить пользователя
    @PostMapping
    public User add(@Valid @RequestBody User user) {
        return userStorage.add(user);
    }

    // Обновить пользователя
    @PutMapping
    public User update(@Valid @RequestBody User user) {
        validateUserExists(user.getId());

        return userStorage.update(user);
    }

    // Получить пользователя по id
    @GetMapping("/{id}")
    public User get(@PathVariable Integer id) {
        validateUserExists(id);

        return userService.get(id);
    }

    // Добавление в друзья
    @PutMapping("/{id}/friends/{friendId}")
    public String addFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        if (id.equals(friendId)) {
            String message = "Нельзя добавить себя в друзья.";
            log.error(message);
            throw new IncorrectPathVariableException(message);
        }

        validateUserExists(id);
        validateUserExists(friendId);

        return userService.addFriend(id, friendId);
    }

    // Удаление из друзей
    @DeleteMapping("/{id}/friends/{friendId}")
    public String deleteFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        validateUserExists(id);
        validateUserExists(friendId);

        return userService.deleteFriend(id, friendId);
    }

    // Получение списка всех друзей пользователя
    @GetMapping("/{id}/friends")
    public List<User> getAllFriends(@PathVariable Integer id) {
        validateUserExists(id);

        return userService.getAllFriends(id);
    }

    // Получение списка друзей, общих с другим пользователем
    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        validateUserExists(id);
        validateUserExists(otherId);

        return userService.getCommonFriends(id, otherId);
    }

    private void validateUserExists(int user) {
        if (!userStorage.getUsers().containsKey(user)) {
            String message = "Пользователя c таким ID не существует.";
            log.error(message);
            throw new UserDoesNotExistException(message);
        }
    }
}
