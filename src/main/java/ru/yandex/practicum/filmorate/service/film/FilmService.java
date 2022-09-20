package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.IncorrectPathVariableException;
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
        return filmStorage.getFilms().get(id);
    }

    // Поставить лайк фильму
    public Film likeFilm(Integer filmId, Integer userId) {
        Film film = get(filmId);
        film.getLikes().add(userId);
        log.trace("Пользователю с id {} понравился фильм с id {}.", userId, filmId);
        return film;
    }

    // Удалить лайк фильма
    public Film dislikeFilm(Integer filmId, Integer userId) {
        Film film = get(filmId);

        if (!film.getLikes().contains(userId)) {
            String message = String.format("Пользователь с id %s попытался удалить лайк у фильма с id %s, которому он не ставил лайк.", userId, filmId);
            log.error(message);
            throw new IncorrectPathVariableException(message);
        }

        film.getLikes().remove(userId);
        log.trace("Пользователь с id {} удалил лайк у фильма с id {}.", userId, filmId);

        return film;
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
        return f1.getLikes().size() - f0.getLikes().size();
    }
}
