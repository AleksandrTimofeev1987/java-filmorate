package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Repository("ResultDBEditor")
public class ResultDbEditor {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ResultDbEditor(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private void setMpaName(Film film) {
        log.trace("FilmDbStorage: Получен запрос на установление значения поля name у рейтинга фильма с ID - {}.", film.getId());

        String sql = "SELECT rating " +
                "FROM mpa " +
                "WHERE mpa_id = ?";
        String rating = jdbcTemplate.queryForObject(sql, RowMapper::mapRowToRating, film.getMpa().getId());
        film.getMpa().setName(rating);
    }

    public void setLikes(Film film) {
        log.trace("FilmDbStorage: Получен запрос на установление значений поля likes у фильма с ID - {}.", film.getId());
        String sql = "SELECT user_id " +
                "FROM film_likes " +
                "WHERE film_id = ?";
        Set<Integer> likes = new HashSet<>(jdbcTemplate.query(sql, RowMapper::mapRowToLikes, film.getId()));
        film.setLikes(likes);
    }

    public void setGenre(Film film) {
        log.trace("FilmDbStorage: Получен запрос на установление значений поля name у жанров фильма с ID - {}.", film.getId());
        String sql = "SELECT fg.genre_id, g.genre " +
                "FROM film_genre AS fg " +
                "LEFT JOIN genres AS g ON fg.genre_id = g.genre_id " +
                "WHERE film_id = ?";
        Set<Genre> genre = new HashSet<>(jdbcTemplate.query(sql, RowMapper::mapRowToGenre, film.getId()));
        film.setGenre(genre);
    }

    public List<Film> setMpaLikesGenre(List<Film> films) {
        films.forEach(this::setMpaName);
        log.trace("FilmDbStorage: Установлены значения поля name у рейтингов полученных фильмов.");
        films.forEach(this::setLikes);
        log.trace("FilmDbStorage: Установлены значения поля likes у полученных фильмов.");
        films.forEach(this::setGenre);
        log.trace("FilmDbStorage: Установлены значения поля name у жанров полученных фильмов.");

        return films;
    }

    public void setFriends(User user) {
        int id = user.getId();
        log.trace("UserDbStorage: Получен запрос на обновление поля friends пользователя c ID - {}.", id);
        String sql = "SELECT friend_id " +
                "FROM user_friends " +
                "WHERE user_id = ?";
        Set<Integer> friends = new HashSet<>(jdbcTemplate.query(sql, RowMapper::mapRowToFriendId, id));
        user.setFriends(friends);
    }
}
