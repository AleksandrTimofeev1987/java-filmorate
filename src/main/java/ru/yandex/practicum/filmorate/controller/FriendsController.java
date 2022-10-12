package ru.yandex.practicum.filmorate.controller;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.IncorrectPathVariableException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FriendsService;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users/{id}/friends")
@Data
public class FriendsController {

    FriendsService friendsService;

    @Autowired
    public FriendsController(FriendsService friendsService) {
        this.friendsService = friendsService;
    }

    // Добавление в друзья
    @PutMapping("/{friendId}")
    public List<User> addFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.trace("FriendsController: Получен запрос от пользователя c ID {} на добавление в друзья пользователя с ID {}.", id, friendId);
        if (id.equals(friendId)) {
            String message = "FriendsController: Нельзя добавить себя в друзья.";
            log.error(message);
            throw new IncorrectPathVariableException(message);
        }
        log.trace("FriendsController: Запрос прошел валидацию на неравнство ID пользователя и потенциального друга.");
        return friendsService.addFriend(id, friendId);
    }

    // Удаление из друзей
    @DeleteMapping("/{friendId}")
    public List<User> deleteFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.trace("FriendsController: Получен запрос от пользователя c ID {} на удаление из друзей пользователя с ID {}.", id, friendId);
        return friendsService.deleteFriend(id, friendId);
    }

    // Получение списка всех друзей пользователя
    @GetMapping
    public List<User> getAllFriends(@PathVariable Integer id) {
        log.trace("FriendsController: Получен запрос на получение всех друзей пользователя c ID {}.", id);
        return friendsService.getAllFriends(id);
    }

    // Получение списка друзей, общих с другим пользователем
    @GetMapping("/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        log.trace("FriendsController: Получен запрос на получение общих друзей пользователей c ID {} и {}.", id, otherId);
        return friendsService.getCommonFriends(id, otherId);
    }
}
