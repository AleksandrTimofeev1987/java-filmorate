package ru.yandex.practicum.filmorate.controller;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.FilmDoesNotExistException;
import ru.yandex.practicum.filmorate.exceptions.FilmValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/films")
@Data

public class FilmController {

    private static final LocalDate EARLIEST_FILM = LocalDate.of(1895, 12, 28);
    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 0;

    // Получить список всех фильмов
    @GetMapping
    public List<Film> getAll() {
        List<Film> allFilms = new ArrayList<>(films.values());

        log.trace("Текущее количество фильмов - {}", films.size());
        return allFilms;
    }

    // Добавить фильм
    @PostMapping
    public Film add(@Valid @RequestBody Film film) {
        if (film.getReleaseDate().isBefore(EARLIEST_FILM)) {
            log.error("Введена дата релиза фильма ранее 28 декабря 1895 года.");
            throw new FilmValidationException("Дата релиза фильма должна быть — не раньше 28 декабря 1895 года.");
        }

        id++;
        film.setId(id);
        films.put(id, film);
        System.out.println(films.values());
        log.trace("Добавлен фильм с id - {}", id);
        return film;
    }

    // Обновить фильм
    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        if (film.getReleaseDate().isBefore(EARLIEST_FILM)) {
            log.error("Введена дата релиза фильма ранее 28 декабря 1895 года.");
            throw new FilmValidationException("Дата релиза фильма должна быть — не раньше 28 декабря 1895 года.");
        }

        int id = film.getId();

        if (!films.containsKey(id)) {
            String message = "Фильм c таким ID не существует.";
            log.error(message);
            throw new FilmDoesNotExistException(message);
        }

        films.replace(id, film);
        log.trace("Обновлен фильм с id - {}", id);
        return film;
    }

}
