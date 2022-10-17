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
        log.debug("AbstractService: Получен запрос к сервису на получение всех данных из базы фильмов или пользователей.");
        return storage.getAll();
    }

    // Добавить данные
    public T add(T data) {
       log.debug("AbstractService: Получен запрос к сервису на добавление {}.", data.getClass().getSimpleName());
       return storage.add(data);
    }

    // Обновить данные
    public T update(T data) {
        log.debug("AbstractService: Получен запрос к сервису на обновление {}.", data.getClass().getSimpleName());
        validateDataExists(data.getId());
        return storage.update(data);
    }

    // Получить данные по ID
    public T get(int id) {
        log.debug("AbstractService: Получен запрос к сервису на получение данных с ID - {}.", id);
        validateDataExists(id);
        return storage.get(id);
    }

    public T delete(int id) {
        log.debug("AbstractService: Получен запрос к сервису на удаление данных с ID - {}.", id);
        validateDataExists(id);
        return storage.delete(id);
    }

    public abstract void validateDataExists(Integer id);

}
