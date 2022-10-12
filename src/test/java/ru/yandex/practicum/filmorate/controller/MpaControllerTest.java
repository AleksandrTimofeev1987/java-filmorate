package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.exceptions.MpaDoesNotExistException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class MpaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Проверка получения списка всех рейтингов
    @Test
    public void shouldReturn200andListOnGetAll() throws Exception {
        //when
        mockMvc.perform(
                        get("/mpa")
                )

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("G"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("PG"))
                .andExpect(jsonPath("$[2].id").value(3))
                .andExpect(jsonPath("$[2].name").value("PG-13"))
                .andExpect(jsonPath("$[3].id").value(4))
                .andExpect(jsonPath("$[3].name").value("R"))
                .andExpect(jsonPath("$[4].id").value(5))
                .andExpect(jsonPath("$[4].name").value("NC-17"));
    }

    // Проверка получения рейтинга по валидному id
    @Test
    public void shouldReturn200AndMpaOnGetMpaWhenValidId() throws Exception {
        //given
        Integer id = 1;

        //when
        mockMvc.perform(
                        get("/mpa/{id}", id)
                )

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("G"));
    }

    // Проверка получения рейтинга по неправильному id (ожидается статус 404 Not Found и GenreDoesNotExistException)
    @Test
    public void shouldReturn404OnGetFilmWhenInvalidFilmId() throws Exception {
        Integer id = 6;

        //when
        mockMvc.perform(
                        get("/mpa/{id}", id)
                )

                //then
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MpaDoesNotExistException))
                .andExpect(result -> assertEquals("MpaService: Рейтинга c таким ID не существует.",
                        result.getResolvedException().getMessage()));
    }
}
