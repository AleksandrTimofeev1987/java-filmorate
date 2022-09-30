package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.StorageData;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.List;

@Slf4j
public abstract class AbstractService<T extends StorageData> {

    Storage<T> storage;

    // Получить список всех данных
    public List<T> getAll() {
        return storage.getAll();
    }

    // Добавить данные
    public T add(T data) {
       return storage.add(data);
    }

    // Обновить данные
    public T update(T data) {
        validateDataExists(data.getId());
        return storage.update(data);
    }

    // Получить данные по ID
    public T get(int id) {
        validateDataExists(id);
        return storage.get(id);
    }

    public T delete(int id) {
        validateDataExists(id);
        return storage.delete(id);
    }

    public abstract void validateDataExists(Integer id);

}
