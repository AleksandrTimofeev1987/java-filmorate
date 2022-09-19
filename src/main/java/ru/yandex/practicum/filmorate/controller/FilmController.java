package ru.yandex.practicum.filmorate.controller;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.FilmDoesNotExistException;
import ru.yandex.practicum.filmorate.exceptions.FilmValidationException;
import ru.yandex.practicum.filmorate.exceptions.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exceptions.UserDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/films")
@Data

public class FilmController {

    private static final LocalDate EARLIEST_FILM = LocalDate.of(1895, 12, 28);
    private final InMemoryFilmStorage filmStorage;
    private final FilmService filmService;
    private final InMemoryUserStorage userStorage;

    @Autowired
    public FilmController(InMemoryFilmStorage filmStorage, FilmService filmService, InMemoryUserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
        this.userStorage = userStorage;
    }

    // Получить список всех фильмов
    @GetMapping
    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    // Добавить фильм
    @PostMapping
    public Film add(@Valid @RequestBody Film film) {
        validateFilmReleaseDate(film);

        return filmStorage.add(film);
    }

    // Обновить фильм
    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        validateFilmReleaseDate(film);

        validateFilmExists(film.getId());

        return filmStorage.update(film);
    }


    // Получить фильм по id
    @GetMapping("/{id}")
    public Film get(@PathVariable Integer id) {
        validateFilmExists(id);

        return filmService.get(id);
    }

    // Поставить лайк фильму
    @PutMapping("/{id}/like/{userId}")
    public String likeFilm(@PathVariable Integer id, @PathVariable Integer userId) {
        validateFilmExists(id);
        validateUserExists(userId);

        return filmService.likeFilm(id, userId);
    }

    // Удалить лайк фильма
    @DeleteMapping("/{id}/like/{userId}")
    public String dislikeFilm(@PathVariable Integer id, @PathVariable Integer userId) {
        validateFilmExists(id);
        validateUserExists(userId);

        return filmService.dislikeFilm(id, userId);
    }

    // Получить count фильмов по кол-ву лайков
    @GetMapping("/popular")
    public List<Film> getMostPopularFilms(@RequestParam(defaultValue = "10", required = false) Integer count) {
        if (count <= 0) {
            String message = "Параметр count должен быть положительным.";
            log.error(message);
            throw new IncorrectParameterException(message);
        }

        return filmService.getMostPopularFilms(count);
    }

    private void validateFilmExists(@PathVariable Integer id) {
        if (!filmStorage.getFilms().containsKey(id)) {
            String message = "Фильм c таким ID не существует.";
            log.error(message);
            throw new FilmDoesNotExistException(message);
        }
    }

    private void validateUserExists(@PathVariable Integer id) {
        if (!userStorage.getUsers().containsKey(id)) {
            String message = "Пользователя c таким ID не существует.";
            log.error(message);
            throw new UserDoesNotExistException(message);
        }
    }

    private static void validateFilmReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(EARLIEST_FILM)) {
            log.error("Введена дата релиза фильма ранее 28 декабря 1895 года.");
            throw new FilmValidationException("Дата релиза фильма должна быть — не раньше 28 декабря 1895 года.");
        }
    }
}
