package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.exceptions.FilmDoesNotExistException;
import ru.yandex.practicum.filmorate.exceptions.FilmValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;

@SpringBootTest
@AutoConfigureMockMvc
public class FilmControllerTest {

    private static final LocalDate RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private static final LocalDate FUTURE_DATE = LocalDate.now().plusDays(1);
    private static final Film VALID_FILM = new Film(1, "film", RandomString.make(200), RELEASE_DATE, 1);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FilmController filmController;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    public void afterEach() {
        filmController.getFilms().clear();
        filmController.setId(0);
    }

    // Проверка добавления валидного фильма
    @Test
    public void filmWhenPostThenStatus200andFilmReturned() throws Exception {
        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(VALID_FILM))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("film"))
                .andExpect(jsonPath("$.duration").value(1));
    }

    // Проверка обновления валидного фильма
    @Test
    public void filmWhenPutThenStatus200andFilmReturned() throws Exception {
        postValidFilm();

        Film updatedFilm = new Film(1, "updated film", RandomString.make(200), RELEASE_DATE, 2);

        mockMvc.perform(
                put("/films")
                        .content(objectMapper.writeValueAsString(updatedFilm))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("updated film"))
                .andExpect(jsonPath("$.duration").value(2));
    }

    // Проверка добавления фильма с датой релиза ранее 28 декабря 1895 года (ожидается статус 400 Bad Request и FilmValidationException)
    @Test
    public void filmWhenPostWithWrongDateThenStatus400andFilmValidationException() throws Exception {
        Film film = new Film(1, "film", RandomString.make(200), RELEASE_DATE.minusDays(1), 1);

        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof FilmValidationException))
                .andExpect(result -> assertEquals("Дата релиза фильма должна быть — не раньше 28 декабря 1895 года.",
                        result.getResolvedException().getMessage()));
    }

    // Проверка обновления фильма с датой релиза ранее 28 декабря 1895 года (ожидается статус 400 Bad Request и FilmValidationException)
    @Test
    public void filmWhenPutWithWrongDateThenStatus400andFilmValidationException() throws Exception {
        postValidFilm();

        Film updatedFilm = new Film(1, "film", RandomString.make(200), RELEASE_DATE.minusDays(1), 1);

        mockMvc.perform(
                put("/films")
                        .content(objectMapper.writeValueAsString(updatedFilm))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof FilmValidationException))
                .andExpect(result -> assertEquals("Дата релиза фильма должна быть — не раньше 28 декабря 1895 года.",
                        result.getResolvedException().getMessage()));
    }

    // Проверка обновления фильма с несуществующим id (ожидается статус 400 Bad Request и FilmDoesNotExistException)
    @Test
    public void filmWhenPutNonExistentThenStatus404andFilmDoesNotExistException() throws Exception {
        postValidFilm();

        Film updatedFilm = new Film(2, "updated film", RandomString.make(200), RELEASE_DATE, 1);

        mockMvc.perform(
                put("/films")
                        .content(objectMapper.writeValueAsString(updatedFilm))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof FilmDoesNotExistException))
                .andExpect(result -> assertEquals("Фильм c таким ID не существует.",
                        result.getResolvedException().getMessage()));
    }

    // Проверка добавления фильма с пустым названием (ожидается статус 400 Bad Request)
    @Test
    public void filmWhenPostWithBlankNameThenStatus400() throws Exception {
        Film film = new Film(1, "", RandomString.make(200), RELEASE_DATE, 1);

        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest());
    }

    // Проверка обновления фильма с пустым названием (ожидается статус 400 Bad Request)
    @Test
    public void filmWhenPutWithBlankNameThenStatus400() throws Exception {
        postValidFilm();

        Film updatedFilm = new Film(1, "", RandomString.make(200), RELEASE_DATE, 1);

        mockMvc.perform(
                put("/films")
                        .content(objectMapper.writeValueAsString(updatedFilm))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest());
    }

    // Проверка добавления фильма с длиной описания более 200 символов (ожидается статус 400 Bad Request)
    @Test
    public void filmWhenPostWithLongDescriptionThenStatus400() throws Exception {
        Film film = new Film(1, "film", RandomString.make(201), RELEASE_DATE, 1);

        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest());
    }

    // Проверка обновления фильма с длиной описания более 200 символов (ожидается статус 400 Bad Request)
    @Test
    public void filmWhenPutWithLongDescriptionThenStatus400() throws Exception {
        postValidFilm();

        Film updatedFilm = new Film(1, "film", RandomString.make(201), RELEASE_DATE, 1);

        mockMvc.perform(
                put("/films")
                        .content(objectMapper.writeValueAsString(updatedFilm))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest());
    }

    // Проверка добавления фильма с датой релиза в будущем (ожидается статус 400 Bad Request)
    @Test
    public void filmWhenPostFutureReleaseDateDescriptionThenStatus400() throws Exception {
        Film film = new Film(1, "film", RandomString.make(200), FUTURE_DATE, 1);

        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest());
    }

    // Проверка обновления фильма с датой релиза в будущем (ожидается статус 400 Bad Request)
    @Test
    public void filmWhenPutFutureReleaseDateDescriptionThenStatus400() throws Exception {
        postValidFilm();

        Film updatedFilm = new Film(1, "film", RandomString.make(200), FUTURE_DATE, 1);

        mockMvc.perform(
                put("/films")
                        .content(objectMapper.writeValueAsString(updatedFilm))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest());
    }

    // Проверка добавления фильма с отрицательной продолжительностью (ожидается статус 400 Bad Request)
    @Test
    public void filmWhenPostNegativeDurationDescriptionThenStatus400() throws Exception {
        Film film = new Film(1, "film", RandomString.make(200), RELEASE_DATE, -1);

        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest());
    }

    // Проверка обновления фильма с отрицательной продолжительностью (ожидается статус 400 Bad Request)
    @Test
    public void filmWhenPutNegativeDurationDescriptionThenStatus400() throws Exception {
        postValidFilm();

        Film updatedFilm = new Film(1, "film", RandomString.make(200), RELEASE_DATE, -1);

        mockMvc.perform(
                put("/films")
                        .content(objectMapper.writeValueAsString(updatedFilm))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest());
    }

    // Проверка добавления фильма с нулевой продолжительностью (ожидается статус 400 Bad Request)
    @Test
    public void filmWhenPostZeroDurationDescriptionThenStatus400() throws Exception {
        Film film = new Film(1, "film", RandomString.make(200), RELEASE_DATE, 0);

        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest());
    }

    // Проверка обновления фильма с нулевой продолжительностью (ожидается статус 400 Bad Request)
    @Test
    public void filmWhenPutZeroDurationDescriptionThenStatus400() throws Exception {
        postValidFilm();

        Film updatedFilm = new Film(1, "film", RandomString.make(200), RELEASE_DATE, 0);

        mockMvc.perform(
                put("/films")
                        .content(objectMapper.writeValueAsString(updatedFilm))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest());
    }

    private void postValidFilm() throws Exception {
        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(VALID_FILM))
                        .contentType(MediaType.APPLICATION_JSON)
        );
    }
}
