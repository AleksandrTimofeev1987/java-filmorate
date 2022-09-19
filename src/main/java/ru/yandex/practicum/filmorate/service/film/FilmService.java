package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {

    private final InMemoryFilmStorage filmStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    // Получить фильм по id
    public Film get(Integer id) {
        log.trace("Получение фильма с id - {}", id);
        return getFilmByID(id);
    }

    // Поставить лайк фильму
    public String likeFilm(Integer filmId, Integer userId) {
        Film film = getFilmByID(filmId);
        film.getLikes().add(userId);
        log.trace("Пользователю с id {} понравился фильм с id {}.", userId, filmId);
        return String.format("Пользователю с id %d понравился фильм с id %d.", userId, filmId);
    }

    // Удалить лайк фильма
    public String dislikeFilm(Integer filmId, Integer userId) {
        Film film = getFilmByID(filmId);
        film.getLikes().remove(userId);
        log.trace("Пользователь с id {} удалил лайк у фильма с id {}.", userId, filmId);
        return String.format("Пользователь с id %d удалил лайк у фильма с id %d.", userId, filmId);
    }

    // Получить count фильмов по кол-ву лайков
    public List<Film> getMostPopularFilms(Integer count) {
        log.trace("Получение списка самых популярных фильмов размером {}.", count);
        return filmStorage.getFilms().values().stream()
                .sorted(this::compare)
                .limit(count)
                .collect(Collectors.toList());
    }

    private int compare(Film f0, Film f1) {
        return f1.getLikes().size()-f0.getLikes().size();
    }

    private Film getFilmByID(Integer id) {
        return filmStorage.getFilms().get(id);
    }
}
