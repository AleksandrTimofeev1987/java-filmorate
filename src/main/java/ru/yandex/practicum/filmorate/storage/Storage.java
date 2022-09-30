package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.StorageData;

import java.util.List;

public interface Storage<T extends StorageData> {

    List<T> getAll();

    T add(T data);

    T update(T data);

    T get(int id);

    T delete (int id);

    boolean validateDataExists(int id);

}
