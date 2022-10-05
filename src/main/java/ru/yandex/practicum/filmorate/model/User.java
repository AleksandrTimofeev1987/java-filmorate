package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class User extends StorageData {

    @NotBlank (message = "Email пользователя не может быть пустым.")
    @Email (message = "Email пользователя должен быть валидным.")
    private String email;

    @NotBlank (message = "Логин пользователя не может быть пустым.")
    @Pattern(regexp = "^\\S*$", message = "Логин не должен содержать пробелы.")
    private String login;

    private String name;

    @Past (message = "Дата рождения не может быть в будущем.")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    private final Set<Integer> friends = new HashSet<>();

    public User(Integer id, String email, String login, String name, LocalDate birthday) {
        super(id);
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }
}
