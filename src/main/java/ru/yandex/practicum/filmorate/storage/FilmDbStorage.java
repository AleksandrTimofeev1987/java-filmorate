package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private static final String SQL_VALIDATE_EXISTS = "SELECT COUNT(*) AS count " +
            "FROM films " +
            "WHERE film_id = ?";
    private static final String SQL_GET_BY_ID = "SELECT film_id, film_name, film_description, release_date, duration, rate " +
            "FROM films " +
            "WHERE film_id = ?";
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> getAll() {
        String sql = "SELECT * " +
                "FROM films";
        List<Film> result = jdbcTemplate.query(sql, RowMapper::mapRowToFilm);
        result.forEach(film -> setLikes(film));
//        result.forEach(film -> setGenre(film));
        return result;
    }

    @Override
    public Film add(Film data) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        int id = simpleJdbcInsert.executeAndReturnKey(data.toMap()).intValue();
        data.setId(id);
        System.out.println(data);
        return data;
    }

    @Override
    public Film update(Film data) {
        String sql = "UPDATE films " +
                "SET film_name = ?, film_description = ?, release_date = ?, duration = ?, rate = ? "
                + "WHERE film_id = ?";
        jdbcTemplate.update(sql,
                data.getName(),
                data.getDescription(),
                data.getReleaseDate(),
                data.getDuration(),
                data.getRate(),
                data.getId());
        return data;
    }

    @Override
    public Film get(int id) {
        Film result = jdbcTemplate.queryForObject(SQL_GET_BY_ID, RowMapper::mapRowToFilm, id);
        setLikes(result);
        return result;
    }

    @Override
    public Film delete(int id) {
        String sqlDelete = "DELETE FROM films WHERE film_id = ?";
        Film deletedFilm = get(id);
        jdbcTemplate.update(sqlDelete, id);
        return deletedFilm;
    }

    @Override
    public boolean validateDataExists(int id) {
        int count = jdbcTemplate.queryForObject(SQL_VALIDATE_EXISTS, RowMapper::mapRowToCount, id);
        return count != 0;
    }

    private void setLikes(Film film) {
        String sql = "SELECT user_id " +
                "FROM film_likes " +
                "WHERE film_id = ?";
        Set<Integer> likes = new HashSet<>(jdbcTemplate.query(sql, RowMapper::mapRowToLikes, film.getId()));
        film.setLikes(likes);
    }
    private void setGenre(Film film) {
        String sql = "SELECT genre_id, genre " +
                "FROM film_genre AS fg " +
                "LEFT JOIN genres AS g on fg.GENRE_ID = g.genre " +
                "WHERE film_id = ?";
        Set<Genre> genre = new HashSet<>(jdbcTemplate.query(sql, RowMapper::mapRowToGenre, film.getId()));
//        film.setGenre(genre);
    }
}
