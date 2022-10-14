package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@Component
@Slf4j
public class FilmValidator {

    private static final LocalDate EARLIEST_FILM = LocalDate.of(1895, 12, 28);

    public void validateFilmReleaseDate(Film film) {
        log.debug("FilmController: Поступил запрос на валидацию даты релиза фильма {}.", film.getName());
        if (film.getReleaseDate().isBefore(EARLIEST_FILM)) {
            log.warn("Введена дата релиза фильма ранее 28 декабря 1895 года.");
            throw new FilmValidationException("Дата релиза фильма должна быть — не раньше 28 декабря 1895 года.");
        }
    }
}
