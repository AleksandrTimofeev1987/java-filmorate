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
public class Film extends StorageData {

    @NotBlank (message = "Название фильма не может быть пустым.")
    private String name;

    @NotNull
    @Size(max = 200, message = "Максимальная длина описания фильма — 200 символов.")
    private String description;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Past (message = "Дата релиза фильма не может быть в будущем.")
    private LocalDate releaseDate;

    @NotNull
    @Positive (message = "Продолжительность фильма должна быть положительной.")
    private long duration;

    private Rating rating;

    private final Set<Integer> likes = new HashSet<>();

    private final Set<Genre> genre = new HashSet<>();

    public Film(Integer id, String name, String description, LocalDate releaseDate, long duration, Rating rating) {
        super(id);
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.rating = rating;
    }
}
