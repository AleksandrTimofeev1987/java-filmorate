package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Repository("FilmDbStorage")
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private static final String SQL_VALIDATE_EXISTS = "SELECT COUNT(*) AS count " +
            "FROM films " +
            "WHERE film_id = ?";
    private static final String SQL_GET_BY_ID = "SELECT f.film_id, f.film_name, f.film_description, f.release_date, f.duration, f.rate, f.mpa_id, m.rating " +
            "FROM films AS f " +
            "LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
            "WHERE film_id = ?";
    private final JdbcTemplate jdbcTemplate;
    private final ResultDbEditor resultDbEditor;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, ResultDbEditor resultDbEditor) {
        this.jdbcTemplate = jdbcTemplate;
        this.resultDbEditor = resultDbEditor;
    }

    @Override
    public List<Film> getAll() {
        log.debug("FilmDbStorage: Получен запрос к хранилищу на получение всех фильмов.");
        String sql = "SELECT * " +
                "FROM films as f " +
                "LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id ";
        List<Film> result = jdbcTemplate.query(sql, RowMapper::mapRowToFilm);

        log.debug("FilmDbStorage: Получен список всех фильмов длиной {}.", result.size());
        return resultDbEditor.setMpaLikesGenre(result);
    }

    @Override
    public Film add(Film data) {
        //Добавляем фильм в БД
        log.debug("FilmDbStorage: Получен запрос к хранилищу на добавление фильма {}.", data.getName());
        SimpleJdbcInsert filmJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        int id = filmJdbcInsert.executeAndReturnKey(data.toMap()).intValue();
        data.setId(id);
        log.trace("FilmDbStorage: В хранилище добавлен фильм с ID - {}.", id);

        //Добавляем жанры в БД
        updateGenre(data);
        log.debug("FilmDbStorage: В хранилище жанров добавлен фильм с ID - {} и его жанры.", id);

        return get(id);
    }

    @Override
    public Film update(Film data) {
        // Обновляем фильм
        int id = data.getId();
        log.debug("FilmDbStorage: Получен запрос к хранилищу на обновление фильма с ID - {}.", id);

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
        log.trace("FilmDbStorage: В хранилище обновлен фильм с ID - {}.", id);

        //Удаляем жанры у фильма
        deleteFilmGenre(id);

        //Добавляем новые жанры в БД
        updateGenre(data);
        log.debug("FilmDbStorage: В хранилище жанров добавлен фильм с ID - {} и его жанры.", id);

        return get(data.getId());
    }

    @Override
    public Film get(int id) {
        log.debug("FilmDbStorage: Получен запрос к хранилищу на получение фильма с ID - {}.", id);
        Film result = jdbcTemplate.queryForObject(SQL_GET_BY_ID, RowMapper::mapRowToFilm, id);
        log.trace("FilmDbStorage: Получен фильм с ID - {}.", result.getId());
        resultDbEditor.setLikes(result);
        log.trace("FilmDbStorage: Установлены значения списка лайков фильма с ID - {}.", result.getId());
        resultDbEditor.setGenre(result);
        log.debug("FilmDbStorage: Установлены значения списка жанров фильма с ID - {}.", result.getId());
        return result;
    }

    @Override
    public Film delete(int id) {
        log.debug("FilmDbStorage: Получен запрос к хранилищу на удаление фильма с ID - {}.", id);

        //Удаляем ссылку на фильм из film_genre
        String sqlDeleteGenre = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(sqlDeleteGenre, id);
        log.trace("FilmDbStorage: Удалены ссылки на фильм с ID - {} из хранилища жанров.", id);

        //Удаляем ссылку на фильм из film_likes
        String sqlDeleteLike = "DELETE FROM film_likes WHERE film_id = ?";
        jdbcTemplate.update(sqlDeleteLike, id);
        log.trace("FilmDbStorage: Удалены ссылки на фильм с ID - {} из хранилища лайков.", id);

        //Удаляем фильм
        String sqlDelete = "DELETE FROM films WHERE film_id = ?";
        Film deletedFilm = get(id);
        jdbcTemplate.update(sqlDelete, id);
        log.debug("FilmDbStorage: Удален фильм с ID - {}.", id);
        return deletedFilm;
    }

    @Override
    public boolean validateDataExists(int id) {
        log.debug("FilmDbStorage: Поступил запрос сервиса на проверку наличия фильма с ID {} в базе данных фильмов.", id);
        int count = jdbcTemplate.queryForObject(SQL_VALIDATE_EXISTS, RowMapper::mapRowToCount, id);
        log.debug("FilmDbStorage: Получен ответ хранилища на запрос сервиса на проверку наличия фильма с ID {} в базе данных фильмов. Наличие записей с нужным ID - {}", id, count);
        return count != 0;
    }

    private void deleteFilmGenre(int id) {
        log.trace("FilmDbStorage: Получен запрос на удаление фильма с ID - {} в хранилище жанров.", id);
        String sqlDeleteGenres = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(sqlDeleteGenres, id);
    }

    private void updateGenre(Film data) {
        int id = data.getId();
        log.debug("FilmDbStorage: Получен запрос на обновление жанров фильма с ID - {}.", id);

        String sqlAddGenre = "INSERT INTO film_genre(film_id, genre_id) " +
                "values (?, ?)";

        for (Genre genre : data.getGenres()) {
            jdbcTemplate.update(sqlAddGenre,
                    id,
                    genre.getId());
        }
    }
}
