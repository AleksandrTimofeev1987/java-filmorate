package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

//@Component
public class InMemoryUserStorage extends AbstractStorage<User>implements UserStorage {

    @Override
    public List<User> getMostPopularFilms(int count) {
        return null;
    }
}
