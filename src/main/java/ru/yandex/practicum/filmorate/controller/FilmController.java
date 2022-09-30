package ru.yandex.practicum.filmorate.controller;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/films")
@Data

public class FilmController {

    private final FilmService filmService;
    private final FilmValidator filmValidator;

    @Autowired
    public FilmController(FilmService filmService, FilmValidator filmValidator) {
        this.filmService = filmService;
        this.filmValidator = filmValidator;
    }

    // Получить список всех фильмов
    @GetMapping
    public List<Film> getAll() {
        return filmService.getAll();
    }

    // Добавить фильм
    @PostMapping
    public Film add(@Valid @RequestBody Film film) {
        filmValidator.validateFilmReleaseDate(film);

        return filmService.add(film);
    }

    // Обновить фильм
    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        filmValidator.validateFilmReleaseDate(film);

        return filmService.update(film);
    }


    // Получить фильм по id
    @GetMapping("/{id}")
    public Film get(@PathVariable Integer id) {

        return filmService.get(id);
    }

    // Удалить фильм по id
    @DeleteMapping("/{id}")
    public Film delete(@PathVariable Integer id) {

        return filmService.delete(id);
    }

    // Поставить лайк фильму
    @PutMapping("/{id}/like/{userId}")
    public Film likeFilm(@PathVariable Integer id, @PathVariable Integer userId) {

        return filmService.likeFilm(id, userId);
    }

    // Удалить лайк фильма
    @DeleteMapping("/{id}/like/{userId}")
    public Film dislikeFilm(@PathVariable Integer id, @PathVariable Integer userId) {

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
