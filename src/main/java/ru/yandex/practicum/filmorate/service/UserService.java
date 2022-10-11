package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserDoesNotExistException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
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
        validateNullNameAndSetLoginAsName(user);
        return storage.add(user);
    }

    // Обновить пользователя
    @Override
    public User update(User user) {
        validateDataExists(user.getId());
        validateNullNameAndSetLoginAsName(user);
        return storage.update(user);
    }


    @Override
    public void validateDataExists(Integer id) {
        if (!storage.validateDataExists(id)) {
            String message = "Пользователя c таким ID не существует.";
            log.error(message);
            throw new UserDoesNotExistException(message);
        }
    }

    private static void validateNullNameAndSetLoginAsName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
