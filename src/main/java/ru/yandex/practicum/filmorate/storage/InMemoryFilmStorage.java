package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

//@Component
public class InMemoryFilmStorage extends AbstractStorage<Film> implements FilmStorage {

    @Override
    public List<Film> getMostPopularFilms(int count) {
        return null;
    }
}
