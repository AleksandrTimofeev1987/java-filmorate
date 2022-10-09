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
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService extends AbstractService<User> {

    FriendStorage friendStorage;

    @Autowired
    public UserService(@Qualifier("UserDbStorage") Storage<User> storage, FriendStorage friendStorage) {
        this.storage = storage;
        this.friendStorage = friendStorage;
    }

    // Добавить пользователя
    @Override
    public User add(User user) {
        validateNullNameAndSetLoginAsName(user);
        return storage.add(user);
    }

    // Обновить пользователя
    @Override
    public User update(User user) {
        validateDataExists(user.getId());
        validateNullNameAndSetLoginAsName(user);
        return storage.update(user);
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

    @Override
    public void validateDataExists(Integer id) {
        if (!storage.validateDataExists(id)) {
            String message = "Пользователя c таким ID не существует.";
            log.error(message);
            throw new UserDoesNotExistException(message);
        }
    }

    private List<User> getUsersList(Set<Integer> tempSet) {
        return storage.getAll()
                .stream()
                .filter(user -> tempSet.contains(user.getId()))
                .collect(Collectors.toList());
    }

    private static void validateNullNameAndSetLoginAsName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
