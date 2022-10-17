package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Genre extends StorageData {

    private String name;

    public Genre(Integer id, String name) {
        super(id);
        this.name = name;
    }
}
