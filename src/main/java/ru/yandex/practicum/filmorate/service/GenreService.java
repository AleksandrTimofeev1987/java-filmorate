package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.GenreDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;

import java.util.List;

@Service
@Slf4j
public class GenreService {

    private final GenreDbStorage genreStorage;

    @Autowired
    public GenreService(@Qualifier("GenreStorage") GenreDbStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public List<Genre> getAll() {
        log.trace("GenreService: Получен запрос к сервису на получение всех жанров.");
        return genreStorage.getAll();
    }

    public Genre get(Integer id) {
        log.trace("GenreService: Получен запрос к сервису на получение жанра с ID - {}.", id);
        validateDataExists(id);
        log.trace("GenreService: Пройдена валидация сервиса на наличие жанра с ID {} в базе данных.", id);
        return genreStorage.get(id);
    }

    public void validateDataExists(Integer id) {
        log.trace("GenreService: Поступил запрос на проверку наличия жанра с ID {} в базе данных жанров.", id);
        if (!genreStorage.validateDataExists(id)) {
            String message = "GenreService: Жанра c таким ID не существует.";
            log.error(message);
            throw new GenreDoesNotExistException(message);
        }
    }
}
