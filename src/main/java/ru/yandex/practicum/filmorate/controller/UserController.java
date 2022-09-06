package ru.yandex.practicum.filmorate.controller;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.UserDoesNotExistException;
import ru.yandex.practicum.filmorate.exceptions.UserValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/users")
@Data

public class UserController {

    private int id = 0;
    private final Map<Integer, User> users = new HashMap<>();

    // Получить список всех пользователей
    @GetMapping
    public List<User> getAllUsers() {
        List<User> allUsers = new ArrayList<>(users.values());

        log.trace("Текущее количество пользователей - {}", users.size());
        return allUsers;
    }

    // Добавить пользователя
    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        if (user.getLogin().contains(" ")) {
            String message = "Логин не должен содержать пробелы.";
            log.error(message);
            throw new UserValidationException(message);
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        id++;
        user.setId(id);
        users.put(id, user);
        System.out.println(users.values());
        log.trace("Добавлен пользователь с id - {}", id);
        return user;
    }

    // Обновить пользователя
    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if (user.getLogin().contains(" ")) {
            throw new UserValidationException("Логин не должен содержать пробелы.");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        int id = user.getId();

        if (!users.containsKey(id)) {
            String message = "Пользователя c таким ID не существует.";
            log.error(message);
            throw new UserDoesNotExistException(message);
        }

        users.replace(id, user);
        log.trace("Обновлен пользователь с id - {}", id);
        return user;
    }

}
