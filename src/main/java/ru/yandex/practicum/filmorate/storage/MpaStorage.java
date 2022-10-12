package ru.yandex.practicum.filmorate.storage;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.List;

@Repository("MpaStorage")
@Slf4j
@Getter
public class MpaStorage {

    private static final String SQL_VALIDATE_EXISTS = "SELECT COUNT(*) AS count " +
            "FROM mpa " +
            "WHERE mpa_id = ?";
    private static final String SQL_GET_BY_ID = "SELECT mpa_id, rating " +
            "FROM mpa " +
            "WHERE mpa_id = ?";

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<MPA> getAll() {
        log.trace("MpaStorage: Получен запрос к хранилищу на получение всех рейтингов.");
        String sql = "SELECT * " +
                "FROM mpa ";
        return jdbcTemplate.query(sql, RowMapper::mapRowToMpa);
    }

    public MPA get(Integer id) {
        log.trace("MpaStorage: Получен запрос к хранилищу на получение рейтинга с ID - {}.", id);
        return jdbcTemplate.queryForObject(SQL_GET_BY_ID, RowMapper::mapRowToMpa, id);
    }

    public boolean validateDataExists(int id) {
        log.trace("MpaStorage: Поступил запрос сервиса на проверку наличия рейтинга с ID {} в базе данных жанров.", id);
        int count = jdbcTemplate.queryForObject(SQL_VALIDATE_EXISTS, RowMapper::mapRowToCount, id);
        log.trace("MpaStorage: Получен ответ хранилища на запрос сервиса на проверку наличия рейтинга с ID {} в базе данных рейтингов. Наличие записей с нужным ID - {}", id, count);
        return count != 0;
    }
}
