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
//TODO: имплементировать разные имплементации
        validateDataExists(userId);
        validateDataExists(friendId);
//
//        User user = get(userId);
//        User friend = get(friendId);
//
//        user.getFriends().add(friendId);
//        friend.getFriends().add(userId);
        List<User> result = friendStorage.addFriend(userId, friendId);

        log.trace("Пользователь с id {} стал другом пользователя с id {}.", userId, friendId);
        return result;
    }

    // Удаление из друзей
    public List<User> deleteFriend(int userId, int friendId) {
        validateDataExists(userId);
        validateDataExists(friendId);
//TODO: имплементировать разные имплементации
//        User user = get(userId);
//        User friend = get(friendId);
//
//        user.getFriends().remove(friendId);
//        friend.getFriends().remove(userId);

        List<User> result = friendStorage.deleteFriend(userId, friendId);

        log.trace("Пользователь с id {} удалил из друзей пользователя с id {}.", userId, friendId);
        return result;
    }

    // Получение списка всех друзей пользователя
    public List<User> getAllFriends(int userId) {
        validateDataExists(userId);
        //TODO: имплементировать разные имплементации
        //Set<Integer> result = get(userId).getFriends();

        List<User> result = friendStorage.getAllFriends(userId);

        log.trace("Количество друзей у пользователя с id {} составляет {}.", userId, result.size());
        return result;
    }

    // Получение списка друзей, общих с другим пользователем
    public List<User> getCommonFriends(int userId, int otherId) {
        validateDataExists(userId);
        validateDataExists(otherId);

        //TODO: имплементировать разные имплементации
//        Set<Integer> userFriends = get(userId).getFriends();
//        Set<Integer> otherFriends = get(otherId).getFriends();

//        Set<Integer> tempSet = new HashSet<>(userFriends);
//        tempSet.retainAll(otherFriends);

        List<User> result = friendStorage.getCommonFriends(userId, otherId);

        log.trace("Получение общих друзей у пользователей с id {} и {}.", userId, otherId);
        return result;
    }

    public void validateDataExists(Integer id) {
        if (!storage.validateDataExists(id)) {
            String message = "Пользователя c таким ID не существует.";
            log.error(message);
            throw new UserDoesNotExistException(message);
        }
    }
}
