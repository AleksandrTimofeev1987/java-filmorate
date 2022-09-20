package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        return userStorage.getUsers().get(id);
    }

    // Добавление в друзья
    public List<User> addFriend(Integer userId, Integer friendId) {
        User user = get(userId);
        User friend = get(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);

        log.trace("Пользователь с id {} стал другом пользователя с id {}.", userId, friendId);
        return List.of(user, friend);
    }

    // Удаление из друзей
    public List<User> deleteFriend(Integer userId, Integer friendId) {
        User user = get(userId);
        User friend = get(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

        log.trace("Пользователь с id {} удалил из друзей пользователя с id {}.", userId, friendId);
        return List.of(user, friend);
    }

    // Получение списка всех друзей пользователя
    public List<User> getAllFriends(Integer userId) {
        Set<Integer> friends = get(userId).getFriends();

        log.trace("Количество друзей у пользователя с id {} составляет {}.", userId, friends.size());
        return getUsersList(friends);
    }

    // Получение списка друзей, общих с другим пользователем
    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        Set<Integer> userFriends = get(userId).getFriends();
        Set<Integer> otherFriends = get(otherId).getFriends();

        Set<Integer> tempSet = new HashSet<>(userFriends);
        tempSet.retainAll(otherFriends);

        log.trace("Получение общих друзей у пользователей с id {} и {}.", userId, otherId);
        return getUsersList(tempSet);
    }

    private List<User> getUsersList(Set<Integer> tempSet) {
        return userStorage.getUsers().values()
                .stream()
                .filter(user -> tempSet.contains(user.getId()))
                .collect(Collectors.toList());
    }
}
