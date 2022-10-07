package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.*;

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

    private int rate = 0;

    @JsonIgnore
    private MPA mpa;

    private final Set<Integer> likes = new HashSet<>();

    private final Set<Genre> genre = new TreeSet<>(Comparator.comparingInt(Genre::getId));

    public Film(Integer id, String name, String description, LocalDate releaseDate, long duration, MPA mpa) {
        super(id);
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
    }

    public Map<String,Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("film_name", name);
        values.put("film_description", description);
        values.put("release_date", releaseDate);
        values.put("duration", duration);
        values.put("rate", rate);
        values.put("mpa_id", mpa.getId());
        return values;
    }
}
