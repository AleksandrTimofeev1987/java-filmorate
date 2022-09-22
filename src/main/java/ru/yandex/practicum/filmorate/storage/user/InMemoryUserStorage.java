package ru.yandex.practicum.filmorate.storage.user;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@Data
public class InMemoryUserStorage implements UserStorage {

    private Integer globalUserId = 0;
    private final Map<Integer, User> users = new HashMap<>();

    // Получить список всех пользователей
    public List<User> getAll() {
        List<User> allUsers = new ArrayList<>(users.values());

        log.trace("Текущее количество пользователей - {}", users.size());
        return allUsers;
    }

    // Добавить пользователя
    public User add(User user) {
        validateNullNameAndSetLoginAsName(user);

        Integer id = getNextId();
        user.setId(id);
        users.put(id, user);
        System.out.println(users.values());
        log.trace("Добавлен пользователь с id - {}", id);
        return user;
    }

    // Обновить пользователя
    public User update(User user) {
        validateNullNameAndSetLoginAsName(user);

        int id = user.getId();

        users.replace(id, user);
        log.trace("Обновлен пользователь с id - {}", id);
        return user;
    }

    private Integer getNextId() {
        return ++globalUserId;
    }

    private static void validateNullNameAndSetLoginAsName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
