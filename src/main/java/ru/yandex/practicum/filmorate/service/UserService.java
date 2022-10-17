package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserDoesNotExistException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storage;

@Service
@Slf4j
public class UserService extends AbstractService<User> {

    @Autowired
    public UserService(@Qualifier("UserDbStorage") Storage<User> storage) {
        this.storage = storage;
    }

    // Добавить пользователя
    @Override
    public User add(User user) {
        log.debug("UserService: Получен запрос к сервису на добавление пользователя с логином {}.", user.getLogin());
        validateNullNameAndSetLoginAsName(user);
        return storage.add(user);
    }

    // Обновить пользователя
    @Override
    public User update(User user) {
        log.debug("UserService: Получен запрос к сервису на обновление пользователя с ID - {}.", user.getId());
        validateDataExists(user.getId());
        validateNullNameAndSetLoginAsName(user);
        return storage.update(user);
    }

    @Override
    public void validateDataExists(Integer id) {
        log.debug("UserService: Поступил запрос на проверку наличия пользователя с ID {} в базе данных пользователей.", id);
        if (!storage.validateDataExists(id)) {
            String message = "UserService: Пользователя c таким ID не существует.";
            log.warn(message);
            throw new UserDoesNotExistException(message);
        }
    }

    private static void validateNullNameAndSetLoginAsName(User user) {
        log.debug("UserService: Поступил запрос на проверку имени пользователя c логином {} на NULL или BLANK.", user.getLogin());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("UserService: Имя пользователя c логином {} теперь равно логину.", user.getLogin());
        }
    }
}
