package ru.yandex.practicum.filmorate.storage;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
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
        String sql = "SELECT * " +
                "FROM mpa ";
        return jdbcTemplate.query(sql, RowMapper::mapRowToMpa);
    }

    public MPA get(Integer id) {
        return jdbcTemplate.queryForObject(SQL_GET_BY_ID, RowMapper::mapRowToMpa, id);
    }

    public boolean validateDataExists(int id) {
        int count = jdbcTemplate.queryForObject(SQL_VALIDATE_EXISTS, RowMapper::mapRowToCount, id);
        return count != 0;
    }
}
