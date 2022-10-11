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
    private static final String SQL_GET_BY_ID = "SELECT f.film_id, f.film_name, f.film_description, f.release_date, f.duration, f.rate, f.mpa_id, m.rating " +
            "FROM films AS f " +
            "LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
            "WHERE film_id = ?";
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> getAll() {
        String sql = "SELECT * " +
                "FROM films as f " +
                "LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id ";
        List<Film> result = jdbcTemplate.query(sql, RowMapper::mapRowToFilm);
        result.forEach(this::setMpaName);
        result.forEach(this::setLikes);
        result.forEach(this::setGenre);
        return result;
    }

    @Override
    public Film add(Film data) {

        //Добавляем фильм в БД
        SimpleJdbcInsert filmJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        int id = filmJdbcInsert.executeAndReturnKey(data.toMap()).intValue();
        data.setId(id);

        //Добавляем жанры в БД
        updateGenre(data);

        return get(id);
    }

    @Override
    public Film update(Film data) {
        // Обновляем фильм
        String sql = "UPDATE films " +
                "SET film_name = ?, film_description = ?, release_date = ?, duration = ?, rate = ?, mpa_id = ? "
                + "WHERE film_id = ?";
        jdbcTemplate.update(sql,
                data.getName(),
                data.getDescription(),
                data.getReleaseDate(),
                data.getDuration(),
                data.getRate(),
                data.getMpa().getId(),
                data.getId());

        //Удаляем жанры у фильма
        String sqlDeleteGenres = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(sqlDeleteGenres, data.getId());

        //Добавляем новые жанры в БД
        updateGenre(data);

        return get(data.getId());
    }

    private void updateGenre(Film data) {
        String sqlAddGenre = "INSERT INTO film_genre(film_id, genre_id) " +
                "values (?, ?)";

        for (Genre genre : data.getGenres()) {
            jdbcTemplate.update(sqlAddGenre,
                    data.getId(),
                    genre.getId());
        }
    }

    @Override
    public Film get(int id) {
        Film result = jdbcTemplate.queryForObject(SQL_GET_BY_ID, RowMapper::mapRowToFilm, id);
        setLikes(result);
        setGenre(result);
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

    public List<Film> getMostPopularFilms(int count) {
        String sql = "SELECT * " +
                "FROM films as f " +
                "LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                "ORDER BY rate DESC " +
                "LIMIT ?";
        List<Film> result = jdbcTemplate.query(sql, RowMapper::mapRowToFilm, count);

        result.forEach(this::setMpaName);
        result.forEach(this::setLikes);
        result.forEach(this::setGenre);

        return result;
    }

    private void setMpaName(Film film) {
        String sql = "SELECT rating " +
                "FROM mpa " +
                "WHERE mpa_id = ?";
        String rating = jdbcTemplate.queryForObject(sql, RowMapper::mapRowToRating, film.getMpa().getId());
        film.getMpa().setName(rating);
    }

    private void setLikes(Film film) {
        String sql = "SELECT user_id " +
                "FROM film_likes " +
                "WHERE film_id = ?";
        Set<Integer> likes = new HashSet<>(jdbcTemplate.query(sql, RowMapper::mapRowToLikes, film.getId()));
        film.setLikes(likes);
    }

    private void setGenre(Film film) {
        String sql = "SELECT fg.genre_id, g.genre " +
                "FROM film_genre AS fg " +
                "LEFT JOIN genres AS g ON fg.genre_id = g.genre_id " +
                "WHERE film_id = ?";
        Set<Genre> genre = new HashSet<>(jdbcTemplate.query(sql, RowMapper::mapRowToGenre, film.getId()));
        film.setGenre(genre);
    }
}
