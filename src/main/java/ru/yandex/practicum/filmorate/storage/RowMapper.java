package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class RowMapper {
    static User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        int id = rs.getInt("user_id");
        String email = rs.getString("email");
        String login = rs.getString("login");
        String name = rs.getString("name");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();
        return new User(id, email, login, name, birthday);
    }

    static Integer mapRowToFriendId(ResultSet rs, int rowNum) throws SQLException {
        return rs.getInt("friend_id");
    }

    static int mapRowToCount(ResultSet rs, int rowNum) throws SQLException {
        return rs.getInt("count");
    }


}
