package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class User {

    private int id;

    @NotBlank (message = "Email пользователя не может быть пустым.")
    @Email (message = "Email пользователя должен быть валидным.")
    private String email;

    @NotBlank (message = "Логин пользователя не может быть пустым.")
    private String login;

    private String name;

    @Past (message = "Дата рождения не может быть в будущем.")
    private LocalDate birthday;
}
