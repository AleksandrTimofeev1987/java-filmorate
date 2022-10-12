package ru.yandex.practicum.filmorate.controller;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/genres")
@Data
public class GenreController {

    private final GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    // Получить список всех жанров
    @GetMapping
    public List<Genre> getAll() {
        log.trace("GenreController: Получен запрос на получение списка всех жанров.");
        return genreService.getAll();
    }

    // Получить жанр по id
    @GetMapping("/{id}")
    public Genre get(@PathVariable Integer id) {
        log.trace("GenreController: Получен запрос на получение жанра с ID {}.", id);
        return genreService.get(id);
    }
}
