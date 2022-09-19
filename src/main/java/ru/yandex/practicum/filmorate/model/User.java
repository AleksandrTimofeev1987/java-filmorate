package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
public class User {

    private int id;

    @NotBlank (message = "Email пользователя не может быть пустым.")
    @Email (message = "Email пользователя должен быть валидным.")
    private String email;

    @NotBlank (message = "Логин пользователя не может быть пустым.")
    @Pattern(regexp = "^\\S*$", message = "Логин не должен содержать пробелы.")
    private String login;

    private String name;

    @Past (message = "Дата рождения не может быть в будущем.")
    private LocalDate birthday;

    private final Set<Integer> friends = new HashSet<>();
}
