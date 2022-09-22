package ru.yandex.practicum.filmorate.storage.film;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@Data
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();
    private Integer globalFilmId = 0;

    // Получить список всех фильмов
    public List<Film> getAll() {
        List<Film> allFilms = new ArrayList<>(films.values());

        log.trace("Текущее количество фильмов - {}", films.size());
        return allFilms;
    }

    // Добавить фильм
    public Film add(Film film) {
        Integer id = getNextId();
        film.setId(id);
        films.put(film.getId(), film);
        System.out.println(films.values());
        log.trace("Добавлен фильм с id - {}", id);
        return film;
    }

    // Обновить фильм
    public Film update(Film film) {
        int id = film.getId();

        films.replace(id, film);
        log.trace("Обновлен фильм с id - {}", id);
        return film;
    }

    private Integer getNextId() {
        return ++globalFilmId;
    }
}
