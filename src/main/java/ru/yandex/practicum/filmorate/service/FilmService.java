package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.Storage;

@Service
@Slf4j
public class FilmService extends AbstractService<Film> {

    @Autowired
    public FilmService(@Qualifier("FilmDbStorage") Storage<Film> storage) {
        this.storage = storage;
    }

    @Override
    public void validateDataExists(Integer id) {
        log.debug("FilmService: Поступил запрос на проверку наличия фильма с ID {} в базе данных фильмов.", id);
        if (!storage.validateDataExists(id)) {
            String message = "FilmService: Фильм c таким ID не существует.";
            log.warn(message);
            throw new FilmDoesNotExistException(message);
        }
    }
}
