package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmDoesNotExistException;
import ru.yandex.practicum.filmorate.exceptions.IncorrectPathVariableException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.LikesStorage;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService extends AbstractService<Film> {

    private final UserService userService;
    private final LikesStorage likesStorage;

    @Autowired
    public FilmService(@Qualifier("FilmDbStorage") Storage<Film> storage, UserService userService, @Qualifier("LikesStorage") LikesStorage likesStorage) {
        this.storage = storage;
        this.userService = userService;
        this.likesStorage = likesStorage;
    }

    // Поставить лайк фильму
    public Film likeFilm(int filmId, int userId) {
        validateDataExists(filmId);

        userService.validateDataExists(userId);

//        TODO: имплементировать разные имплементации
//        Film film = get(filmId);
//        film.getLikes().add(userId);
//        film.setRate(film.getLikes().size());

        Film film = likesStorage.likeFilm(filmId, userId);
        log.trace("Пользователю с id {} понравился фильм с id {}.", userId, filmId);
        return film;
    }

    // Удалить лайк фильма
    public Film dislikeFilm(int filmId, int userId) {
        validateDataExists(filmId);
        userService.validateDataExists(userId);

        Film film = get(filmId);

        if (!film.getLikes().contains(userId)) {
            String message = String.format("Пользователь с id %s попытался удалить лайк у фильма с id %s, которому он не ставил лайк.", userId, filmId);
            log.error(message);
            throw new IncorrectPathVariableException(message);
        }
//        TODO: имплементировать разные имплементации
//        film.getLikes().remove(userId);
//        film.setRate(film.getLikes().size());

        Film updatedFilm = likesStorage.dislikeFilm(filmId, userId);

        log.trace("Пользователь с id {} удалил лайк у фильма с id {}.", userId, filmId);

        return updatedFilm;
    }

    // Получить count фильмов по кол-ву лайков
    public List<Film> getMostPopularFilms(int count) {
        log.trace("Получение списка самых популярных фильмов размером {}.", count);
//        return storage.getAll().stream()
//                .sorted(this::compare)
//                .limit(count)
//                .collect(Collectors.toList());
        return storage.getMostPopularFilms(count);
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
