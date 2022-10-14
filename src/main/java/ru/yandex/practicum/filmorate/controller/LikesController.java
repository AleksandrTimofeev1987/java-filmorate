package ru.yandex.practicum.filmorate.controller;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.LikesService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/films")
@Data
public class LikesController {

    private final LikesService likesService;

    @Autowired
    public LikesController(LikesService likesService) {
        this.likesService = likesService;
    }

    // Поставить лайк фильму
    @PutMapping("/{id}/like/{userId}")
    public Film likeFilm(@PathVariable Integer id, @PathVariable Integer userId) {
        log.debug("LikesController: Получен запрос от пользователя с ID {} на лайк фильма с ID {}.", userId, id);
        return likesService.likeFilm(id, userId);
    }

    // Удалить лайк фильма
    @DeleteMapping("/{id}/like/{userId}")
    public Film dislikeFilm(@PathVariable Integer id, @PathVariable Integer userId) {
        log.debug("LikesController: Получен запрос от пользователя с ID {} на удаление лайка фильма с ID {}.", userId, id);
        return likesService.dislikeFilm(id, userId);
    }

    // Получить count фильмов по кол-ву лайков
    @GetMapping("/popular")
    public List<Film> getMostPopularFilms(@RequestParam(defaultValue = "10", required = false) Integer count) {
        log.debug("LikesController: Получен запрос на получение списка {} самых популярных фильмов.", count);
        validateCountParameter(count);

        return likesService.getMostPopularFilms(count);
    }

    private static void validateCountParameter(Integer count) {
        log.trace("LikesController: Поступил запрос на проверку валидности параметра запроса count.");
        if (count <= 0) {
            String message = "LikesController: Параметр count должен быть положительным.";
            log.warn(message);
            throw new IncorrectParameterException(message);
        }
    }


}
