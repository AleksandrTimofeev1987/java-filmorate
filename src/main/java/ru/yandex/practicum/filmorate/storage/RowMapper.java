package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

@Slf4j
public class RowMapper {
    public static User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        log.debug("RowMapper: Получен запрос от хранилища на преобразование данных в экземляр пользователя (User).");
        int id = rs.getInt("user_id");
        String email = rs.getString("email");
        String login = rs.getString("login");
        String name = rs.getString("user_name");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();
        return new User(id, email, login, name, birthday);
    }

    public static Integer mapRowToFriendId(ResultSet rs, int rowNum) throws SQLException {
        log.debug("RowMapper: Получен запрос от хранилища на преобразование данных в ID друга (friendId).");
        return rs.getInt("friend_id");
    }

    public static int mapRowToCount(ResultSet rs, int rowNum) throws SQLException {
        log.debug("RowMapper: Получен запрос от хранилища на преобразование данных в количество записей (count).");
        return rs.getInt("count");
    }

    public static Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        log.debug("RowMapper: Получен запрос от хранилища на преобразование данных в экземляр фильма (Film).");
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
        log.debug("RowMapper: Получен запрос от хранилища на преобразование данных в лайк (userId).");
        return rs.getInt("user_id");
    }

    public static int mapRowToLikedId(ResultSet rs, int rowNum) throws SQLException {
        log.debug("RowMapper: Получен запрос от хранилища на преобразование данных в ID понравившегося фильма (filmId).");
        return rs.getInt("film_id");
    }

    public static String mapRowToRating(ResultSet rs, int rowNum) throws SQLException {
        log.debug("RowMapper: Получен запрос от хранилища на преобразование данных в количество лайков (rate).");
        return rs.getString("rating");
    }

    public static MPA mapRowToMpa(ResultSet rs, int rowNum) throws SQLException {
        log.debug("RowMapper: Получен запрос от хранилища на преобразование данных в экземпляр рейтинга (MPA).");
        int id = rs.getInt("mpa_id");
        String rating = rs.getString("rating");
        return new MPA(id, rating);
    }

    public static Genre mapRowToGenre(ResultSet rs, int rowNum) throws SQLException {
        log.debug("RowMapper: Получен запрос от хранилища на преобразование данных в экземпляр жанра (Genre).");
        int id = rs.getInt("genre_id");
        String genre = rs.getString("genre");
        return new Genre(id, genre);
    }


}
