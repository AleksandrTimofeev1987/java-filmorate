package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Repository("UserDbStorage")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public List<User> getAll() {
        String sql = "SELECT user_id, email, name, login, birthday " +
                "FROM users";
        return jdbcTemplate.query(sql, this::mapRowToUser);
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
                "email = ?, "
        return null;
    }

    @Override
    public User get(int id) {
        String sql = "SELECT user_id, email, name, login, birthday " +
                "FROM users " +
                "WHERE user_id = ?";
        return jdbcTemplate.queryForObject(sql, this::mapRowToUser, id);
    }

    @Override
    public User delete(int id) {
        return null;
    }

    @Override
    public boolean validateDataExists(int id) {
        //TODO: имплементировать
        return true;
    }

    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        int id = rs.getInt("user_id");
        String email = rs.getString("email");
        String login = rs.getString("login");
        String name = rs.getString("name");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();
        return new User(id, email, login, name, birthday);
    }
}
