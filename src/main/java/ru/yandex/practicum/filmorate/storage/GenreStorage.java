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
public class GenreStorage {

    private static final String SQL_VALIDATE_EXISTS = "SELECT COUNT(*) AS count " +
            "FROM genres " +
            "WHERE genre_id = ?";
    private static final String SQL_GET_BY_ID = "SELECT genre_id, genre " +
            "FROM genres " +
            "WHERE genre_id = ?";
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Genre> getAll() {
        String sql = "SELECT * " +
                "FROM genres ";
        return jdbcTemplate.query(sql, RowMapper::mapRowToGenre);
    }

    public Genre get(Integer id) {
        return jdbcTemplate.queryForObject(SQL_GET_BY_ID, RowMapper::mapRowToGenre, id);
    }

    public boolean validateDataExists(int id) {
        int count = jdbcTemplate.queryForObject(SQL_VALIDATE_EXISTS, RowMapper::mapRowToCount, id);
        return count != 0;
    }
}
