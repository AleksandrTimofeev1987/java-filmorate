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
        if (!storage.validateDataExists(id)) {
            String message = "Фильм c таким ID не существует.";
            log.error(message);
            throw new FilmDoesNotExistException(message);
        }
    }

    //TODO: правильно ли?
    private int compare(Film f0, Film f1) {
        return f0.getRate() - f1.getRate();
    }
}
