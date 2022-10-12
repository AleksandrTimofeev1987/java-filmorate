package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository("UserDbStorage")
@Slf4j
public class UserDbStorage implements UserStorage {

    private static final String SQL_GET_BY_ID = "SELECT user_id, email, name, login, birthday " +
            "FROM users " +
            "WHERE user_id = ?";
    private static final String SQL_VALIDATE_EXISTS = "SELECT COUNT(*) AS count " +
            "FROM users " +
            "WHERE user_id = ?";
    private final JdbcTemplate jdbcTemplate;
    private final LikesStorage likesStorage;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate, LikesStorage likesStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.likesStorage = likesStorage;
    }

    @Override
    public List<User> getAll() {
        log.trace("UserDbStorage: Получен запрос к сервису на получение всех пользователей из базы пользователей.");
        String sql = "SELECT * " +
                "FROM users";
        List<User> result = jdbcTemplate.query(sql, RowMapper::mapRowToUser);
        log.trace("UserDbStorage: Получен список всех пользователей из базы пользователей размером {}.", result.size());

        result.forEach(this::setFriends);
        log.trace("UserDbStorage: Обновлены поля friends полученного списка всех пользователей.");

        return result;
    }

    @Override
    public User add(User data) {
        log.trace("UserDbStorage: Получен запрос к хранилищу на добавление пользователя с логином {}.", data.getLogin());
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        int id = simpleJdbcInsert.executeAndReturnKey(data.toMap()).intValue();
        log.trace("UserDbStorage: В хранилище добавлен пользователь с ID - {}.", id);
        data.setId(id);
        return get(id);
    }

    @Override
    public User update(User data) {
        int id = data.getId();
        log.trace("UserDbStorage: Получен запрос к хранилищу на обновление пользователя с ID - {}.", id);
        String sql = "UPDATE users SET " +
                "email = ?, name = ?, login = ?, birthday = ? "
                + "WHERE user_id = ?";
        jdbcTemplate.update(sql,
                data.getEmail(),
                data.getName(),
                data.getLogin(),
                data.getBirthday(),
                data.getId());
        log.trace("UserDbStorage: В хранилище обновлен пользователь с ID - {}.", id);

        return get(id);
    }

    @Override
    public User get(int id) {
        log.trace("UserDbStorage: Получен запрос к хранилищу на получение пользователя с ID - {}.", id);
        User result = jdbcTemplate.queryForObject(SQL_GET_BY_ID, RowMapper::mapRowToUser, id);
        log.trace("UserDbStorage: Получен пользователь с ID - {}.", result.getId());

        setFriends(result);
        log.trace("UserDbStorage: Обновлено поле friends полученного пользователя.");

        return result;
    }

    @Override
    public User delete(int id) {
        //Удалаяем ссылку на пользователя из user_friends
        log.trace("UserDbStorage: Получен запрос к хранилищу на удаление пользователя с ID - {}.", id);
        String sqlDeleteFriendship = "DELETE FROM user_friends WHERE friend_id = ?";

        //Удалаяем ссылку на пользователя из user_friends
        jdbcTemplate.update(sqlDeleteFriendship, id);
        log.trace("UserDbStorage: Удалены ссылки на пользователя с ID - {} из хранилища дружбы.", id);

        //Удалаяем ссылку на пользователя из film_likes и обновляем rate у фильмов, которые понравились пользователю
        String sqlGetLikedFilms = "SELECT film_id FROM film_likes WHERE user_id = ?";
        List<Integer> likedFilms = jdbcTemplate.query(sqlGetLikedFilms, RowMapper::mapRowToLikedId, id);
        likedFilms.forEach(filmId -> likesStorage.dislikeFilm(filmId, id));
        String sqlDeleteLike = "DELETE FROM film_likes WHERE user_id = ?";
        jdbcTemplate.update(sqlDeleteLike, id);
        log.trace("UserDbStorage: Удалены ссылки на пользователя с ID - {} из хранилища лайков.", id);

        // Удаляем пользователя
        String sqlDeleteUser = "DELETE FROM users WHERE user_id = ?";
        User deletedUser = get(id);
        jdbcTemplate.update(sqlDeleteUser, id);
        log.trace("UserDbStorage: Удален пользователь с ID - {}.", deletedUser.getId());

        return deletedUser;
    }

    @Override
    public boolean validateDataExists(int id) {
        log.trace("UserDbStorage: Поступил запрос сервиса на проверку наличия пользователя с ID {} в базе данных пользователей.", id);
        int count = jdbcTemplate.queryForObject(SQL_VALIDATE_EXISTS, RowMapper::mapRowToCount, id);
        log.trace("UserDbStorage: Получен ответ хранилища на запрос сервиса на проверку наличия пользователя с ID {} в базе данных пользователей. Наличие записей с нужным ID - {}", id, count);
        return count != 0;
    }

    @Override
    public List<User> getMostPopularFilms(int count) {
        return null;
    }

    private void setFriends(User user) {
        int id = user.getId();
        log.trace("UserDbStorage: Получен запрос на обновление поля friends пользователя c ID - {}.", id);
        String sql = "SELECT friend_id " +
                "FROM user_friends " +
                "WHERE user_id = ?";
        Set<Integer> friends = new HashSet<>(jdbcTemplate.query(sql, RowMapper::mapRowToFriendId, id));
        user.setFriends(friends);
    }
}
