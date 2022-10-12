package ru.yandex.practicum.filmorate.controller;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/mpa")
@Data
public class MpaController {

    private final MpaService mpaService;

    @Autowired
    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    // Получить список всех рейтингов
    @GetMapping
    public List<MPA> getAll() {
        log.trace("MpaController: Получен запрос на получение списка всех рейтингов.");
        return mpaService.getAll();
    }

    // Получить рейтинг по id
    @GetMapping("/{id}")
    public MPA get(@PathVariable Integer id) {
        log.trace("MpaController: Получен запрос на получение рейтинга с ID {}.", id);
        return mpaService.get(id);
    }
}

