package ru.yandex.practicum.filmorate.controller;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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
        log.debug("FilmController: Получен запрос на получение списка всех фильмов.");
        return filmService.getAll();
    }

    // Добавить фильм
    @PostMapping
    public Film add(@Valid @RequestBody Film film) {
        log.debug("FilmController: Получен запрос на добавление фильма {}.", film.getName());
        filmValidator.validateFilmReleaseDate(film);
        return filmService.add(film);
    }

    // Обновить фильм
    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.debug("FilmController: Получен запрос на обновление фильма {} с ID - {}.", film.getName(), film.getId());
        filmValidator.validateFilmReleaseDate(film);
        return filmService.update(film);
    }


    // Получить фильм по id
    @GetMapping("/{id}")
    public Film get(@PathVariable Integer id) {
        log.debug("FilmController: Получен запрос на получение фильма с ID - {}.", id);
        return filmService.get(id);
    }

    // Удалить фильм по id
    @DeleteMapping("/{id}")
    public Film delete(@PathVariable Integer id) {
        log.debug("FilmController: Получен запрос на удаление фильма с ID - {}.", id);
        return filmService.delete(id);
    }
}
