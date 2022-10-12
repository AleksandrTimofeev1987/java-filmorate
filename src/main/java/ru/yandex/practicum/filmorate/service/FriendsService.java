package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserDoesNotExistException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.List;

@Service
@Slf4j
public class FriendsService {

    private final Storage<User> storage;
    private final FriendStorage friendStorage;

    @Autowired
    public FriendsService(@Qualifier("UserDbStorage") Storage<User> storage, FriendStorage friendStorage) {
        this.storage = storage;
        this.friendStorage = friendStorage;
    }

    // Добавление в друзья
    public List<User> addFriend(int userId, int friendId) {
        log.trace("FriendsService: Получен запрос к сервису от пользователя c ID {} на добавление в друзья пользователя с ID {}.", userId, friendId);
        validateDataExists(userId);
        log.trace("FriendsService: Пройдена проверка наличия пользователя с ID {} в базе данных пользователей.", userId);
        validateDataExists(friendId);
        log.trace("FriendsService: Пройдена проверка наличия пользователя (потенциального друга) с ID {} в базе данных пользователей.", friendId);
//TODO: имплементировать разные имплементации
//        User user = get(userId);
//        User friend = get(friendId);
//
//        user.getFriends().add(friendId);
//        friend.getFriends().add(userId);
        List<User> result = friendStorage.addFriend(userId, friendId);

        log.trace("FriendsService: Пользователь с id {} стал другом пользователя с id {}.", userId, friendId);
        return result;
    }

    // Удаление из друзей
    public List<User> deleteFriend(int userId, int friendId) {
        log.trace("FriendsService: Получен запрос к сервису от пользователя c ID {} на удаление из друзей пользователя с ID {}.", userId, friendId);
        validateDataExists(userId);
        log.trace("FriendsService: Пройдена проверка наличия пользователя с ID {} в базе данных пользователей.", userId);
        validateDataExists(friendId);
        log.trace("FriendsService: Пройдена проверка наличия пользователя (потенциального друга) с ID {} в базе данных пользователей.", friendId);
//TODO: имплементировать разные имплементации
//        User user = get(userId);
//        User friend = get(friendId);
//
//        user.getFriends().remove(friendId);
//        friend.getFriends().remove(userId);

        List<User> result = friendStorage.deleteFriend(userId, friendId);

        log.trace("FriendsService: Пользователь с id {} удалил из друзей пользователя с id {}.", userId, friendId);
        return result;
    }

    // Получение списка всех друзей пользователя
    public List<User> getAllFriends(int userId) {
        log.trace("FriendsService: Получен запрос к сервису на получение всех друзей пользователя c ID - {}.", userId);
        validateDataExists(userId);
        log.trace("FriendsService: Пройдена проверка наличия пользователя с ID {} в базе данных пользователей.", userId);
        //TODO: имплементировать разные имплементации
        //Set<Integer> result = get(userId).getFriends();

        List<User> result = friendStorage.getAllFriends(userId);

        log.trace("FriendsService: Количество друзей у пользователя с id {} составляет {}.", userId, result.size());
        return result;
    }

    // Получение списка друзей, общих с другим пользователем
    public List<User> getCommonFriends(int userId, int otherId) {
        log.trace("FriendsService: Получен запрос к сервису на получение общих друзей пользователей c ID {} и {}.", userId, otherId);
        validateDataExists(userId);
        log.trace("FriendsService: Пройдена проверка наличия пользователя с ID {} в базе данных пользователей.", userId);
        validateDataExists(otherId);
        log.trace("FriendsService: Пройдена проверка наличия пользователя с ID {} в базе данных пользователей.", otherId);

        //TODO: имплементировать разные имплементации
//        Set<Integer> userFriends = get(userId).getFriends();
//        Set<Integer> otherFriends = get(otherId).getFriends();

//        Set<Integer> tempSet = new HashSet<>(userFriends);
//        tempSet.retainAll(otherFriends);

        List<User> result = friendStorage.getCommonFriends(userId, otherId);

        log.trace("FriendsService: Получены общие друзья в количестве {} у пользователей с id {} и {}.", result.size(), userId, otherId);
        return result;
    }

    public void validateDataExists(Integer id) {
        log.trace("FriendsService: Поступил запрос на проверку наличия пользователя с ID {} в базе данных пользователей.", id);
        if (!storage.validateDataExists(id)) {
            String message = "FriendsService: Пользователя c таким ID не существует.";
            log.error(message);
            throw new UserDoesNotExistException(message);
        }
    }
}
