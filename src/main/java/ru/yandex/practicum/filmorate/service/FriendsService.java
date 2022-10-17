package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserDoesNotExistException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendDbStorage;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.List;

@Service
@Slf4j
public class FriendsService {

    private final Storage<User> storage;
    private final FriendDbStorage friendStorage;

    @Autowired
    public FriendsService(@Qualifier("UserDbStorage") Storage<User> storage, FriendDbStorage friendStorage) {
        this.storage = storage;
        this.friendStorage = friendStorage;
    }

    // Добавление в друзья
    public List<User> addFriend(int userId, int friendId) {
        log.debug("FriendsService: Получен запрос к сервису от пользователя c ID {} на добавление в друзья пользователя с ID {}.", userId, friendId);
        validateDataExists(userId);
        validateDataExists(friendId);
        List<User> result = friendStorage.addFriend(userId, friendId);

        log.debug("FriendsService: Пользователь с id {} стал другом пользователя с id {}.", userId, friendId);
        return result;
    }

    // Удаление из друзей
    public List<User> deleteFriend(int userId, int friendId) {
        log.debug("FriendsService: Получен запрос к сервису от пользователя c ID {} на удаление из друзей пользователя с ID {}.", userId, friendId);
        validateDataExists(userId);
        validateDataExists(friendId);

        List<User> result = friendStorage.deleteFriend(userId, friendId);

        log.debug("FriendsService: Пользователь с id {} удалил из друзей пользователя с id {}.", userId, friendId);
        return result;
    }

    // Получение списка всех друзей пользователя
    public List<User> getAllFriends(int userId) {
        log.debug("FriendsService: Получен запрос к сервису на получение всех друзей пользователя c ID - {}.", userId);
        validateDataExists(userId);

        List<User> result = friendStorage.getAllFriends(userId);

        log.debug("FriendsService: Количество друзей у пользователя с id {} составляет {}.", userId, result.size());
        return result;
    }

    // Получение списка друзей, общих с другим пользователем
    public List<User> getCommonFriends(int userId, int otherId) {
        log.debug("FriendsService: Получен запрос к сервису на получение общих друзей пользователей c ID {} и {}.", userId, otherId);
        validateDataExists(userId);
        validateDataExists(otherId);

        List<User> result = friendStorage.getCommonFriends(userId, otherId);

        log.debug("FriendsService: Получены общие друзья в количестве {} у пользователей с id {} и {}.", result.size(), userId, otherId);
        return result;
    }

    public void validateDataExists(Integer id) {
        log.debug("FriendsService: Поступил запрос на проверку наличия пользователя с ID {} в базе данных пользователей.", id);
        if (!storage.validateDataExists(id)) {
            String message = "FriendsService: Пользователя c таким ID не существует.";
            log.warn(message);
            throw new UserDoesNotExistException(message);
        }
    }
}
