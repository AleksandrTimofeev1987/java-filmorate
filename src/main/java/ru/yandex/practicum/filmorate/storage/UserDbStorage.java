package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository("UserDbStorage")
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
        String sql = "SELECT * " +
                "FROM users";
        List<User> result = jdbcTemplate.query(sql, RowMapper::mapRowToUser);
        result.forEach(this::setFriends);
        return result;
    }

    @Override
    public User add(User data) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        int id = simpleJdbcInsert.executeAndReturnKey(data.toMap()).intValue();
        data.setId(id);
        return data;
    }

    @Override
    public User update(User data) {
        String sql = "UPDATE users SET " +
                "email = ?, name = ?, login = ?, birthday = ? "
                + "WHERE user_id = ?";
        jdbcTemplate.update(sql,
                data.getEmail(),
                data.getName(),
                data.getLogin(),
                data.getBirthday(),
                data.getId());
        return data;
    }

    @Override
    public User get(int id) {
        User result = jdbcTemplate.queryForObject(SQL_GET_BY_ID, RowMapper::mapRowToUser, id);
        setFriends(result);
        return result;
    }

    @Override
    public User delete(int id) {
        //Удалаяем ссылку на пользователя из user_friends
        String sqlDeleteFriendship = "DELETE FROM user_friends WHERE friend_id = ?";
        jdbcTemplate.update(sqlDeleteFriendship, id);

        //Удалаяем ссылку на пользователя из film_likes и обновляем rate у фильмов, которые понравились пользователю
        String sqlGetLikedFilms = "SELECT film_id FROM film_likes WHERE user_id = ?";
        List<Integer> likedFilms = jdbcTemplate.query(sqlGetLikedFilms, RowMapper::mapRowToLikedId, id);
        likedFilms.forEach(filmId -> likesStorage.dislikeFilm(filmId, id));
        String sqlDeleteLike = "DELETE FROM film_likes WHERE user_id = ?";
        jdbcTemplate.update(sqlDeleteLike, id);

        // Удаляем пользователя
        String sqlDeleteUser = "DELETE FROM users WHERE user_id = ?";
        User deletedUser = get(id);
        jdbcTemplate.update(sqlDeleteUser, id);
        return deletedUser;
    }

    @Override
    public boolean validateDataExists(int id) {
        int count = jdbcTemplate.queryForObject(SQL_VALIDATE_EXISTS, RowMapper::mapRowToCount, id);
        return count != 0;
    }

    @Override
    public List<User> getMostPopularFilms(int count) {
        return null;
    }

    private void setFriends(User user) {
        String sql = "SELECT friend_id " +
                "FROM user_friends " +
                "WHERE user_id = ?";
        Set<Integer> friends = new HashSet<>(jdbcTemplate.query(sql, RowMapper::mapRowToFriendId, user.getId()));
        user.setFriends(friends);
    }
}
