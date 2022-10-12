package ru.yandex.practicum.filmorate.controller;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("/users")
@Data
public class UserController {

    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Получить список всех пользователей
    @GetMapping
    public List<User> getAll() {
        log.trace("UserController: Получен запрос на получение списка всех пользователей.");
        return userService.getAll();
    }

    // Добавить пользователя
    @PostMapping
    public User add(@Valid @RequestBody User user) {
        log.trace("UserController: Получен запрос на добавление пользователя c логином {}.", user.getLogin());
        return userService.add(user);
    }

    // Обновить пользователя
    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.trace("UserController: Получен запрос на обновление пользователя c ID - {}.", user.getId());
        return userService.update(user);
    }

    // Получить пользователя по id
    @GetMapping("/{id}")
    public User get(@PathVariable Integer id) {
        log.trace("UserController: Получен запрос на получение пользователя c ID - {}.", id);
        return userService.get(id);
    }

    // Удалить пользователя по id
    @DeleteMapping("/{id}")
    public User delete(@PathVariable Integer id) {
        log.trace("UserController: Получен запрос на удаление пользователя c ID - {}.", id);
        return userService.delete(id);
    }
}
