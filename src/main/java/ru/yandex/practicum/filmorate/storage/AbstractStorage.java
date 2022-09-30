package ru.yandex.practicum.filmorate.storage;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.StorageData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Getter
public abstract class AbstractStorage<T extends StorageData> implements Storage<T> {
    private Integer globalId = 0;
    private final Map<Integer, T> storage = new HashMap<>();

    // Получить список всех данных
    public List<T> getAll() {
        List<T> allData = new ArrayList<>(storage.values());

        log.trace("Текущее количество фильмов - {}", storage.size());
        return allData;
    }

    // Добавить данные
    public T add(T data) {
        int id = getNextId();
        data.setId(id);
        storage.put(data.getId(), data);
        log.trace("Добавлены данные с id - {}", id);
        return data;
    }

    // Обновить данные
    public T update(T data) {
        int id = data.getId();
        storage.replace(id, data);
        log.trace("Обновлен фильм с id - {}", id);
        return data;
    }

    // Получить данные по ID
    public T get(int id) {
        log.trace("Получение данных с id - {}", id);
        return storage.get(id);
    }

    public T delete(int id) {
        log.trace("Удаление данных с id - {}", id);
        return storage.remove(id);
    }

    public boolean validateDataExists(int id) {
        return storage.containsKey(id);
    }

    private Integer getNextId() {
        return ++globalId;
    }


}
