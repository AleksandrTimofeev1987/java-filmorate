package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class UserService {

    private final InMemoryUserStorage userStorage;

    @Autowired
    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    // Получить пользователя по id
    public User get(Integer id) {
        log.trace("Получение пользователя с id - {}", id);
        return getUserByID(id);
    }

    // Добавление в друзья
    public String addFriend(Integer userId, Integer friendId) {
        User user = getUserByID(userId);
        User friend = getUserByID(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);

        log.trace("Пользователь с id {} стал другом пользователя с id {}.", userId, friendId);
        return String.format("Пользователь с id %d стал другом пользователя с id %d.", userId, friendId);
    }

    // Удаление из друзей
    public String deleteFriend(Integer userId, Integer friendId) {
        User user = getUserByID(userId);
        User friend = getUserByID(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

        log.trace("Пользователь с id {} удалил из друзей пользователя с id {}.", userId, friendId);
        return String.format("Пользователь с id %d удалил из друзей пользователя с id %d.", userId, friendId);
    }

    // Получение списка всех друзей пользователя
    public List<User> getAllFriends(Integer userId) {
        Set<Integer> friends = getUserByID(userId).getFriends();

        log.trace("Количество друзей у пользователя с id {} составляет {}.", userId, friends.size());
        return getUsersList(friends);
    }

    // Получение списка друзей, общих с другим пользователем
    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        Set<Integer> userFriends = getUserByID(userId).getFriends();
        Set<Integer> otherFriends = getUserByID(otherId).getFriends();

        Set<Integer> tempSet = new HashSet<>(userFriends);
        tempSet.retainAll(otherFriends);

        log.trace("Получение общих друзей у пользователей с id {} и {}.", userId, otherId);
        return getUsersList(tempSet);
    }

    private List<User> getUsersList(Set<Integer> tempSet) {
        List<User> result = new ArrayList<>();

        for (Integer friendId : tempSet) {
            User friend = getUserByID(friendId);
            if (friend != null) {
                result.add(friend);
            }
        }
        return result;
    }

    private User getUserByID(Integer id) {
        return userStorage.getUsers().get(id);
    }
}
