package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
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

    static Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        int id = rs.getInt("film_id");
        String filmName = rs.getString("film_name");
        String filmDescription = rs.getString("film_description");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        long duration = rs.getLong("duration");
        int rate = rs.getInt("rate");
        MPA mpa = new MPA(rs.getInt("mpa_id"), rs.getString("rating"));
        return new Film(id, filmName, filmDescription, releaseDate, duration, rate, mpa);
    }

    public static int mapRowToLikes(ResultSet rs, int rowNum) throws SQLException {
        return rs.getInt("user_id");
    }

    public static int mapRowToLikedId(ResultSet rs, int rowNum) throws SQLException {
        return rs.getInt("film_id");
    }

    public static String mapRowToRating(ResultSet rs, int rowNum) throws SQLException {
        return rs.getString("rating");
    }

    public static MPA mapRowToMpa(ResultSet rs, int rowNum) throws SQLException {
        int id = rs.getInt("mpa_id");
        String rating = rs.getString("rating");
        return new MPA(id, rating);
    }

    public static Genre mapRowToGenre(ResultSet rs, int rowNum) throws SQLException {
        int id = rs.getInt("genre_id");
        String genre = rs.getString("genre");
        return new Genre(id, genre);
    }


}
