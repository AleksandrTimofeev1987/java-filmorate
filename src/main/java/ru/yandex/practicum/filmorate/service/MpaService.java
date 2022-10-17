package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.MpaDoesNotExistException;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;

import java.util.List;

@Service
@Slf4j
public class MpaService {

    private final MpaDbStorage mpaStorage;

    @Autowired
    public MpaService(@Qualifier("MpaStorage") MpaDbStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public List<MPA> getAll() {
        log.debug("MpaService: Получен запрос к сервису на получение всех рейтингов.");
        return mpaStorage.getAll();
    }

    public MPA get(Integer id) {
        log.debug("MpaService: Получен запрос к сервису на получение рейтинга с ID - {}.", id);
        validateDataExists(id);
        return mpaStorage.get(id);
    }

    public void validateDataExists(Integer id) {
        log.debug("MpaService: Поступил запрос на проверку наличия рейтинга с ID {} в базе данных рейтингов.", id);
        if (!mpaStorage.validateDataExists(id)) {
            String message = "MpaService: Рейтинга c таким ID не существует.";
            log.warn(message);
            throw new MpaDoesNotExistException(message);
        }
    }
}
