package ru.yandex.practicum.filmorate.storage;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository("FriendStorage")
@Slf4j
@Getter
public class FriendStorage {

    Storage<User> storage;

    private final JdbcTemplate jdbcTemplate;


    @Autowired
    public FriendStorage(JdbcTemplate jdbcTemplate, @Qualifier("UserDbStorage") Storage<User> storage) {
        this.storage = storage;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<User> addFriend(int userId, int friendId) {
        // Добавляем запись о дружбе
        String sql = "INSERT INTO user_friends(user_id, friend_id) " +
                "VALUES (?,?)";
        jdbcTemplate.update(sql,
                userId, friendId);

        // Получаем результат
        List<User> result = new ArrayList<>();
        result.add(storage.get(userId));
        result.add(storage.get(friendId));
        return result;
    }

    public List<User> deleteFriend(int userId, int friendId) {
        // Удаляем запись о дружбе
        String sqlDelete = "DELETE FROM user_friends WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sqlDelete, userId, friendId);

        List<User> result = new ArrayList<>();
        result.add(storage.get(userId));
        result.add(storage.get(friendId));
        return result;
    }

    public List<User> getAllFriends(int userId) {
        String sql = "SELECT uf.friend_id, u.USER_ID, u.email, u.name, u.login, u.birthday " +
                "FROM USER_FRIENDS AS uf " +
                "LEFT JOIN users AS u ON uf.FRIEND_ID = u.USER_ID " +
                "WHERE uf.USER_ID = ?";
        List<User> result = jdbcTemplate.query(sql, RowMapper::mapRowToUser, userId);
        result.forEach(user -> setFriends(user));
        return result;
    }


    public List<User> getCommonFriends(int userId, int otherId) {
        // Получаем список ID общих друзей
        String sql = "SELECT friend_id " +
                "FROM user_friends " +
                "WHERE user_id = ? and friend_id IN (SELECT friend_id " +
                "FROM user_friends " +
                "WHERE user_id = ?)";
        List<Integer> common_id = jdbcTemplate.query(sql, RowMapper::mapRowToFriendId, userId, otherId);

        // Получаем список пользователей - общих друзей
        return common_id.stream()
                .map(id -> storage.get(id))
                .collect(Collectors.toList());
    }

    //TODO: убрать повторение кода с UserDbStorage
    private void setFriends(User user) {
        String sql = "SELECT friend_id " +
                "FROM user_friends " +
                "WHERE user_id = ?";
        Set<Integer> friends = new HashSet<>(jdbcTemplate.query(sql, RowMapper::mapRowToFriendId, user.getId()));
        user.setFriends(friends);
    }
}
