package ru.yandex.practicum.filmorate.controller;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.IncorrectPathVariableException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("/users")
@Data

public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Получить список всех пользователей
    @GetMapping
    public List<User> getAll() {
        return userService.getAll();
    }

    // Добавить пользователя
    @PostMapping
    public User add(@Valid @RequestBody User user) {
        return userService.add(user);
    }

    // Обновить пользователя
    @PutMapping
    public User update(@Valid @RequestBody User user) {
        return userService.update(user);
    }

    // Получить пользователя по id
    @GetMapping("/{id}")
    public User get(@PathVariable Integer id) {
        return userService.get(id);
    }

    // Удалить пользователя по id
    @DeleteMapping("/{id}")
    public User delete(@PathVariable Integer id) {
        return userService.delete(id);
    }

    // Добавление в друзья
    @PutMapping("/{id}/friends/{friendId}")
    public List<User> addFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        if (id.equals(friendId)) {
            String message = "Нельзя добавить себя в друзья.";
            log.error(message);
            throw new IncorrectPathVariableException(message);
        }

        return userService.addFriend(id, friendId);
    }

    // Удаление из друзей
    @DeleteMapping("/{id}/friends/{friendId}")
    public List<User> deleteFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        return userService.deleteFriend(id, friendId);
    }

    // Получение списка всех друзей пользователя
    @GetMapping("/{id}/friends")
    public List<User> getAllFriends(@PathVariable Integer id) {
        return userService.getAllFriends(id);
    }

    // Получение списка друзей, общих с другим пользователем
    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        return userService.getCommonFriends(id, otherId);
    }
}
