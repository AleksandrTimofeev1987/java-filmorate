package ru.yandex.practicum.filmorate.controller.film;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controller.user.UserValidator;
import ru.yandex.practicum.filmorate.exceptions.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/films")
@Data

public class FilmController {

    private final InMemoryFilmStorage filmStorage;
    private final FilmService filmService;
    private final InMemoryUserStorage userStorage;
    private final FilmValidator filmValidator;
    private final UserValidator userValidator;

    @Autowired
    public FilmController(InMemoryFilmStorage filmStorage, FilmService filmService, InMemoryUserStorage userStorage, FilmValidator filmValidator, UserValidator userValidator) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
        this.userStorage = userStorage;
        this.filmValidator = filmValidator;
        this.userValidator = userValidator;
    }

    // Получить список всех фильмов
    @GetMapping
    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    // Добавить фильм
    @PostMapping
    public Film add(@Valid @RequestBody Film film) {
        filmValidator.validateFilmReleaseDate(film);

        return filmStorage.add(film);
    }

    // Обновить фильм
    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        filmValidator.validateFilmReleaseDate(film);

        filmValidator.validateFilmExists(film.getId());

        return filmStorage.update(film);
    }


    // Получить фильм по id
    @GetMapping("/{id}")
    public Film get(@PathVariable Integer id) {
        filmValidator.validateFilmExists(id);

        return filmService.get(id);
    }

    // Поставить лайк фильму
    @PutMapping("/{id}/like/{userId}")
    public Film likeFilm(@PathVariable Integer id, @PathVariable Integer userId) {
        filmValidator.validateFilmExists(id);
        userValidator.validateUserExists(userId);

        return filmService.likeFilm(id, userId);
    }

    // Удалить лайк фильма
    @DeleteMapping("/{id}/like/{userId}")
    public Film dislikeFilm(@PathVariable Integer id, @PathVariable Integer userId) {
        filmValidator.validateFilmExists(id);
        userValidator.validateUserExists(userId);

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
}
