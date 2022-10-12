package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.MpaDoesNotExistException;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Service
@Slf4j
public class MpaService {

    private final MpaStorage mpaStorage;

    @Autowired
    public MpaService(@Qualifier("MpaStorage") MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public List<MPA> getAll() {
        log.trace("MpaService: Получен запрос к сервису на получение всех рейтингов.");
        return mpaStorage.getAll();
    }

    public MPA get(Integer id) {
        log.trace("MpaService: Получен запрос к сервису на получение рейтинга с ID - {}.", id);
        validateDataExists(id);
        log.trace("MpaService: Пройдена валидация сервиса на наличие рейтинга с ID {} в базе данных.", id);
        return mpaStorage.get(id);
    }

    public void validateDataExists(Integer id) {
        log.trace("MpaService: Поступил запрос на проверку наличия рейтинга с ID {} в базе данных рейтингов.", id);
        if (!mpaStorage.validateDataExists(id)) {
            String message = "MpaService: Рейтинга c таким ID не существует.";
            log.error(message);
            throw new MpaDoesNotExistException(message);
        }
    }
}
