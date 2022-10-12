package ru.yandex.practicum.filmorate.storage;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Repository("GenreStorage")
@Slf4j
@Getter
public class GenreDbStorage {

    private static final String SQL_VALIDATE_EXISTS = "SELECT COUNT(*) AS count " +
            "FROM genres " +
            "WHERE genre_id = ?";
    private static final String SQL_GET_BY_ID = "SELECT genre_id, genre " +
            "FROM genres " +
            "WHERE genre_id = ?";
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Genre> getAll() {
        log.trace("GenreStorage: Получен запрос к хранилищу на получение всех жанров.");
        String sql = "SELECT * " +
                "FROM genres ";
        return jdbcTemplate.query(sql, RowMapper::mapRowToGenre);
    }

    public Genre get(Integer id) {
        log.trace("GenreStorage: Получен запрос к хранилищу на получение жанра с ID - {}.", id);
        return jdbcTemplate.queryForObject(SQL_GET_BY_ID, RowMapper::mapRowToGenre, id);
    }

    public boolean validateDataExists(int id) {
        log.trace("GenreStorage: Поступил запрос сервиса на проверку наличия жанра с ID {} в базе данных жанров.", id);
        int count = jdbcTemplate.queryForObject(SQL_VALIDATE_EXISTS, RowMapper::mapRowToCount, id);
        log.trace("GenreStorage: Получен ответ хранилища на запрос сервиса на проверку наличия жанра с ID {} в базе данных жанров. Наличие записей с нужным ID - {}", id, count);
        return count != 0;
    }
}
