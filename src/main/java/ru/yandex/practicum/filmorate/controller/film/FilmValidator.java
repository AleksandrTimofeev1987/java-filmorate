package ru.yandex.practicum.filmorate.controller.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.exceptions.FilmDoesNotExistException;
import ru.yandex.practicum.filmorate.exceptions.FilmValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.time.LocalDate;

@Component
@Slf4j
public class FilmValidator {

    private static final LocalDate EARLIEST_FILM = LocalDate.of(1895, 12, 28);
    private final InMemoryFilmStorage filmStorage;

    @Autowired
    public FilmValidator(InMemoryFilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public void validateFilmExists(@PathVariable Integer id) {
        if (!filmStorage.getFilms().containsKey(id)) {
            String message = "Фильм c таким ID не существует.";
            log.error(message);
            throw new FilmDoesNotExistException(message);
        }
    }

    public void validateFilmReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(EARLIEST_FILM)) {
            log.error("Введена дата релиза фильма ранее 28 декабря 1895 года.");
            throw new FilmValidationException("Дата релиза фильма должна быть — не раньше 28 декабря 1895 года.");
        }
    }
}
