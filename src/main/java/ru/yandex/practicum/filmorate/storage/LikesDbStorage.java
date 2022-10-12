package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Repository("LikesStorage")
@Slf4j
public class LikesDbStorage {

    private final Storage<Film> storage;
    private final ResultDbEditor resultDbEditor;
    private final JdbcTemplate jdbcTemplate;



    @Autowired
    public LikesDbStorage(JdbcTemplate jdbcTemplate, @Qualifier("FilmDbStorage") Storage<Film> storage, ResultDbEditor resultDbEditor) {
        this.storage = storage;
        this.jdbcTemplate = jdbcTemplate;
        this.resultDbEditor = resultDbEditor;
    }

    public Film likeFilm(int filmId, int userId) {
        // Добавляем запись о лайке
        log.trace("LikesStorage: Получен запрос к хранилищу от пользователя с ID {} на лайк фильма с ID {}.", userId, filmId);
        String sql = "INSERT INTO film_likes(film_id, user_id) " +
                "VALUES (?,?)";
        jdbcTemplate.update(sql,
                filmId, userId);
        log.trace("LikesStorage: Запись о лайке пользователя с ID {} фильму с ID {} успешно добавлена в хранилище.", userId, filmId);

        // Обновляем параметр rate
        updateRate(filmId, true);
        log.trace("LikesStorage: Поле rate фильма с ID {} успешно обновлено в хранилище.", filmId);

        // Получаем результат
        return storage.get(filmId);
    }

    public Film dislikeFilm(int filmId, int userId) {
        // Удаляем запись о лайке
        log.trace("LikesStorage: Получен запрос к хранилищу от пользователя с ID {} на удаление лайка фильма с ID {}.", userId, filmId);
        String sqlDelete = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlDelete, filmId, userId);
        log.trace("LikesStorage: Запись о лайке пользователя с ID {} фильму с ID {} успешно удалена из хранилища.", userId, filmId);

        // Обновляем параметр rate
        updateRate(filmId, false);
        log.trace("LikesStorage: Поле rate фильма с ID {} успешно обновлено в хранилище.", filmId);

        // Получаем результат
        return storage.get(filmId);
    }

    private void updateRate(int filmId, boolean isIncrease) {
        log.trace("LikesStorage: Получен запрос к хранилищу на обновление поля rate фильма с ID {} и параметром isIncrease = {}.", filmId, isIncrease);
        String sql;
        if (isIncrease) {
            sql = "UPDATE films " +
                    "SET rate = rate + 1 "
                    + "WHERE film_id = ?";
        } else {
            sql = "UPDATE films " +
                    "SET rate = rate - 1 "
                    + "WHERE film_id = ?";
        }

        jdbcTemplate.update(sql,
                filmId);
    }

    public List<Film> getMostPopularFilms(int count) {
        log.trace("FilmDbStorage: Получен запрос к хранилищу на получение списка самых популярных фильмов размером {}.", count);
        String sql = "SELECT * " +
                "FROM films as f " +
                "LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                "ORDER BY rate DESC " +
                "LIMIT ?";
        List<Film> result = jdbcTemplate.query(sql, RowMapper::mapRowToFilm, count);
        log.trace("FilmDbStorage: Получен список самых популярных фильмов длиной {} при запросе списка длиной {}.", result.size(), count);

        return resultDbEditor.setMpaLikesGenre(result);
    }
}
