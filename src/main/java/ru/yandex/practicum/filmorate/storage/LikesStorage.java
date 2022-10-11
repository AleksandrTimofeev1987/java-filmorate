package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

@Repository("LikesStorage")
public class LikesStorage {

    private final Storage<Film> storage;
    private final JdbcTemplate jdbcTemplate;


    @Autowired
    public LikesStorage(JdbcTemplate jdbcTemplate, @Qualifier("FilmDbStorage") Storage<Film> storage) {
        this.storage = storage;
        this.jdbcTemplate = jdbcTemplate;
    }

    public Film likeFilm(int filmId, int userId) {
        // Добавляем запись о лайке
        String sql = "INSERT INTO film_likes(film_id, user_id) " +
                "VALUES (?,?)";
        jdbcTemplate.update(sql,
                filmId, userId);

        // Обновляем параметр rate
        updateRate(filmId, true);

        // Получаем результат
        return storage.get(filmId);
    }

    public Film dislikeFilm(int filmId, int userId) {
        // Удаляем запись о лайке
        String sqlDelete = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlDelete, filmId, userId);

        // Обновляем параметр rate
        updateRate(filmId, false);

        // Получаем результат
        return storage.get(filmId);
    }

    private void updateRate(int filmId, boolean isIncrease) {
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
}
