package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.IncorrectPathVariableException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.LikesDbStorage;

import java.util.List;

@Service
@Slf4j
public class LikesService {

    private final UserService userService;
    private final FilmService filmService;
    private final LikesDbStorage likesStorage;


    @Autowired
    public LikesService(UserService userService, FilmService filmService, @Qualifier("LikesStorage") LikesDbStorage likesStorage) {
        this.userService = userService;
        this.filmService = filmService;
        this.likesStorage = likesStorage;
    }

    // Поставить лайк фильму
    public Film likeFilm(int filmId, int userId) {
        log.debug("LikesService: Получен запрос к сервису от пользователя с ID {} на лайк фильма с ID {}.", userId, filmId);
        filmService.validateDataExists(filmId);
        userService.validateDataExists(userId);

        Film film = likesStorage.likeFilm(filmId, userId);
        log.debug("LikesService: Пользователю с id {} понравился фильм с id {}.", userId, filmId);
        return film;
    }

    // Удалить лайк фильма
    public Film dislikeFilm(int filmId, int userId) {
        log.debug("LikesService: Получен запрос к сервису от пользователя с ID {} на удаление лайка фильма с ID {}.", userId, filmId);
        filmService.validateDataExists(filmId);
        userService.validateDataExists(userId);

        Film film = filmService.get(filmId);

        validateUserLikedFilm(filmId, userId, film);

        Film updatedFilm = likesStorage.dislikeFilm(filmId, userId);

        log.debug("LikesService: Пользователь с id {} удалил лайк у фильма с id {}.", userId, filmId);
        return updatedFilm;
    }

    // Получить count фильмов по кол-ву лайков
    public List<Film> getMostPopularFilms(int count) {
        log.debug("LikesService: Получен запрос к сервису на получение списка самых популярных фильмов размером {}.", count);
        return likesStorage.getMostPopularFilms(count);
    }

    private static void validateUserLikedFilm(int filmId, int userId, Film film) {
        log.debug("LikesService: Поступил запрос на проверку наличия лайка пользователя с ID {} у фильма с ID {}.", userId, filmId);
        if (!film.getLikes().contains(userId)) {
            String message = String.format("LikesService: Пользователь с id %s попытался удалить лайк у фильма с id %s, которому он не ставил лайк.", userId, filmId);
            log.warn(message);
            throw new IncorrectPathVariableException(message);
        }
    }
}
