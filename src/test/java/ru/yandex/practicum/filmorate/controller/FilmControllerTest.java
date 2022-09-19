package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.exceptions.FilmDoesNotExistException;
import ru.yandex.practicum.filmorate.exceptions.FilmValidationException;
import ru.yandex.practicum.filmorate.exceptions.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exceptions.UserDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.Arrays;

@SpringBootTest
@AutoConfigureMockMvc
public class FilmControllerTest {

    private static final LocalDate RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private static final LocalDate FUTURE_DATE = LocalDate.now().plusDays(1);
    private static final Film VALID_FILM = new Film(1, "film", RandomString.make(200), RELEASE_DATE, 1);
    private static final LocalDate BIRTHDAY = LocalDate.now().minusDays(1);
    private static final User VALID_USER = new User(1, "1@yandex.ru", "login", "name", BIRTHDAY);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Проверка получения списка всех фильмов
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenValidFilmWhenGetThenStatus200andListReturned() throws Exception {
        //given
        postValidFilm();

        //when
        mockMvc.perform(
                        get("/films")
                )

                //then
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Arrays.asList(VALID_FILM))))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("film"))
                .andExpect(jsonPath("$[0].releaseDate").value(RELEASE_DATE.toString()))
                .andExpect(jsonPath("$[0].duration").value(1))
                .andExpect(jsonPath("$[0].likes.length()").value(0));
    }

    // Проверка добавления валидного фильма
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenValidFilmWhenPostThenStatus200andFilmReturned() throws Exception {
        //when
        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(VALID_FILM))
                        .contentType(MediaType.APPLICATION_JSON)
        )

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("film"))
                .andExpect(jsonPath("$.releaseDate").value(RELEASE_DATE.toString()))
                .andExpect(jsonPath("$.duration").value(1))
                .andExpect(jsonPath("$.likes.length()").value(0));
    }

    // Проверка обновления валидного фильма
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenValidFilmWhenPutThenStatus200andFilmReturned() throws Exception {
        //given
        postValidFilm();

        Film updatedFilm = new Film(1, "updated film", RandomString.make(200), RELEASE_DATE, 2);

        //when
        mockMvc.perform(
                put("/films")
                        .content(objectMapper.writeValueAsString(updatedFilm))
                        .contentType(MediaType.APPLICATION_JSON)
        )

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("updated film"))
                .andExpect(jsonPath("$.releaseDate").value(RELEASE_DATE.toString()))
                .andExpect(jsonPath("$.duration").value(2))
                .andExpect(jsonPath("$.likes.length()").value(0));
    }

    // Проверка добавления фильма с датой релиза ранее 28 декабря 1895 года (ожидается статус 400 Bad Request и FilmValidationException)
    @Test
    public void givenFilmWithWrongDateWhenPostThenStatus400andFilmValidationException() throws Exception {
        //given
        Film film = new Film(1, "film", RandomString.make(200), RELEASE_DATE.minusDays(1), 1);

        //when
        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON)
        )

                //then
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof FilmValidationException))
                .andExpect(result -> assertEquals("Дата релиза фильма должна быть — не раньше 28 декабря 1895 года.",
                        result.getResolvedException().getMessage()));
    }

    // Проверка обновления фильма с датой релиза ранее 28 декабря 1895 года (ожидается статус 400 Bad Request и FilmValidationException)
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenFilmWithWrongDateWhenPutThenStatus400andFilmValidationException() throws Exception {
        //given
        postValidFilm();

        Film updatedFilm = new Film(1, "film", RandomString.make(200), RELEASE_DATE.minusDays(1), 1);

        //when
        mockMvc.perform(
                put("/films")
                        .content(objectMapper.writeValueAsString(updatedFilm))
                        .contentType(MediaType.APPLICATION_JSON)
        )

                //then
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof FilmValidationException))
                .andExpect(result -> assertEquals("Дата релиза фильма должна быть — не раньше 28 декабря 1895 года.",
                        result.getResolvedException().getMessage()));
    }

    // Проверка обновления фильма с несуществующим id (ожидается статус 404 Not Found и FilmDoesNotExistException)
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenNonExistentFilmWhenPutThenStatus404andFilmDoesNotExistException() throws Exception {
        //given
        postValidFilm();

        Film updatedFilm = new Film(2, "updated film", RandomString.make(200), RELEASE_DATE, 1);

        //when
        mockMvc.perform(
                put("/films")
                        .content(objectMapper.writeValueAsString(updatedFilm))
                        .contentType(MediaType.APPLICATION_JSON)
        )

                //then
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof FilmDoesNotExistException))
                .andExpect(result -> assertEquals("Фильм c таким ID не существует.",
                        result.getResolvedException().getMessage()));
    }

    // Проверка добавления фильма с пустым названием (ожидается статус 400 Bad Request)
    @Test
    public void givenFilmWithBlankNameWhenPostThenStatus400() throws Exception {
        //given
        Film film = new Film(1, "", RandomString.make(200), RELEASE_DATE, 1);

        //when
        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON)
        )

                //then
                .andExpect(status().isBadRequest());
    }

    // Проверка обновления фильма с пустым названием (ожидается статус 400 Bad Request)
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenFilmWithBlankNameWhenPutThenStatus400() throws Exception {
        //given
        postValidFilm();

        Film updatedFilm = new Film(1, "", RandomString.make(200), RELEASE_DATE, 1);

        //when
        mockMvc.perform(
                put("/films")
                        .content(objectMapper.writeValueAsString(updatedFilm))
                        .contentType(MediaType.APPLICATION_JSON)
        )

                //then
                .andExpect(status().isBadRequest());
    }

    // Проверка добавления фильма с длиной описания более 200 символов (ожидается статус 400 Bad Request)
    @Test
    public void givenFilmWithLongDescriptionWhenPostThenStatus400() throws Exception {
        //given
        Film film = new Film(1, "film", RandomString.make(201), RELEASE_DATE, 1);

        //when
        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON)
        )

                //then
                .andExpect(status().isBadRequest());
    }

    // Проверка обновления фильма с длиной описания более 200 символов (ожидается статус 400 Bad Request)
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenFilmWithLongDescriptionWhenPutThenStatus400() throws Exception {
        //given
        postValidFilm();

        Film updatedFilm = new Film(1, "film", RandomString.make(201), RELEASE_DATE, 1);

        //when
        mockMvc.perform(
                put("/films")
                        .content(objectMapper.writeValueAsString(updatedFilm))
                        .contentType(MediaType.APPLICATION_JSON)
        )

                //then
                .andExpect(status().isBadRequest());
    }

    // Проверка добавления фильма с датой релиза в будущем (ожидается статус 400 Bad Request)
    @Test
    public void givenFilmWithFutureReleaseDateWhenPostThenStatus400() throws Exception {
        //given
        Film film = new Film(1, "film", RandomString.make(200), FUTURE_DATE, 1);

        //when
        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON)
        )

                //then
                .andExpect(status().isBadRequest());
    }

    // Проверка обновления фильма с датой релиза в будущем (ожидается статус 400 Bad Request)
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenFilmWithFutureReleaseDateWhenPutThenStatus400() throws Exception {
        //given
        postValidFilm();

        Film updatedFilm = new Film(1, "film", RandomString.make(200), FUTURE_DATE, 1);

        //when
        mockMvc.perform(
                put("/films")
                        .content(objectMapper.writeValueAsString(updatedFilm))
                        .contentType(MediaType.APPLICATION_JSON)
        )

                //then
                .andExpect(status().isBadRequest());
    }

    // Проверка добавления фильма с отрицательной продолжительностью (ожидается статус 400 Bad Request)
    @Test
    public void givenFilmWithNegativeDurationDateWhenPostThenStatus400() throws Exception {
        //given
        Film film = new Film(1, "film", RandomString.make(200), RELEASE_DATE, -1);

        //when
        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON)
        )

                //then
                .andExpect(status().isBadRequest());
    }

    // Проверка обновления фильма с отрицательной продолжительностью (ожидается статус 400 Bad Request)
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenFilmWithNegativeDurationWhenPutThenStatus400() throws Exception {
        //given
        postValidFilm();

        Film updatedFilm = new Film(1, "film", RandomString.make(200), RELEASE_DATE, -1);

        //when
        mockMvc.perform(
                put("/films")
                        .content(objectMapper.writeValueAsString(updatedFilm))
                        .contentType(MediaType.APPLICATION_JSON)
        )

                //then
                .andExpect(status().isBadRequest());
    }

    // Проверка добавления фильма с нулевой продолжительностью (ожидается статус 400 Bad Request)
    @Test
    public void givenFilmWithZeroDurationWhenPostThenStatus400() throws Exception {
        //given
        Film film = new Film(1, "film", RandomString.make(200), RELEASE_DATE, 0);

        //when
        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON)
        )

                //then
                .andExpect(status().isBadRequest());
    }

    // Проверка обновления фильма с нулевой продолжительностью (ожидается статус 400 Bad Request)
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenFilmWithZeroDurationWhenPutThenStatus400() throws Exception {
        //given
        postValidFilm();

        Film updatedFilm = new Film(1, "film", RandomString.make(200), RELEASE_DATE, 0);

        //when
        mockMvc.perform(
                put("/films")
                        .content(objectMapper.writeValueAsString(updatedFilm))
                        .contentType(MediaType.APPLICATION_JSON)
        )

                //then
                .andExpect(status().isBadRequest());
    }

    // Проверка получения фильма по валидному id
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenValidIdWhenGetThenStatus200andFilmReturned() throws Exception {
        //given
        postValidFilm();
        Integer id = 1;

        //when
        mockMvc.perform(
                        get("/films/{id}", id)
                )

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("film"))
                .andExpect(jsonPath("$.releaseDate").value(RELEASE_DATE.toString()))
                .andExpect(jsonPath("$.duration").value(1))
                .andExpect(jsonPath("$.likes.length()").value(0));
    }

    // Проверка получения фильма по неправильному id (ожидается статус 404 Not Found и FilmDoesNotExistException)
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenInvalidIdWhenGetThenStatus404andFilmDoesNotExistException() throws Exception {
        //given
        postValidFilm();
        Integer id = 2;

        //when
        mockMvc.perform(
                        get("/films/{id}", id)
                )

                //then
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof FilmDoesNotExistException))
                .andExpect(result -> assertEquals("Фильм c таким ID не существует.",
                result.getResolvedException().getMessage()));
    }

    // Проверка постановки лайка фильму
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenValidFilmAndUserWhenPutLikeThenStatus200andMessage() throws Exception {
        //given
        postValidFilm();
        postValidUser();
        Integer filmId = 1;
        Integer userId = 1;

        //when
        mockMvc.perform(
                        put("/films/{id}/like/{userId}", filmId, userId)
                )

                //then
                .andExpect(status().isOk())
                .andExpect(content().string("Пользователю с id 1 понравился фильм с id 1."));

        mockMvc.perform(
                        get("/films/{id}", filmId)
                )
                .andExpect(jsonPath("$.likes.length()").value(1))
                .andExpect(jsonPath("$.likes[0]").value(1));
    }

    // Проверка постановки лайка фильму с неправильным id фильма (ожидается статус 404 Not Found и FilmDoesNotExistException)
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenInvalidFilmIdWhenPutLikeThenStatus404andFilmDoesNotExistException() throws Exception {
        //given
        postValidFilm();
        postValidUser();
        Integer filmId = 2;
        Integer userId = 1;

        //when
        mockMvc.perform(
                        put("/films/{id}/like/{userId}", filmId, userId)
                )

                //then
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof FilmDoesNotExistException))
                .andExpect(result -> assertEquals("Фильм c таким ID не существует.",
                        result.getResolvedException().getMessage()));
    }

    // Проверка постановки лайка фильму с неправильным id пользователя (ожидается статус 404 Not Found и UserDoesNotExistException)
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenInvalidUserIdWhenPutLikeThenStatus404andUserDoesNotExistException() throws Exception {
        //given
        postValidFilm();
        postValidUser();
        Integer filmId = 1;
        Integer userId = 2;

        //when
        mockMvc.perform(
                        put("/films/{id}/like/{userId}", filmId, userId)
                )

                //then
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserDoesNotExistException))
                .andExpect(result -> assertEquals("Пользователя c таким ID не существует.",
                        result.getResolvedException().getMessage()));
    }

    // Проверка удаления лайка у фильма
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenValidFilmAndUserWhenDeleteLikeThenStatus200andMessage() throws Exception {
        //given
        postValidFilm();
        postValidUser();
        Integer filmId = 1;
        Integer userId = 1;
        putValidLike(filmId, userId);

        //when
        mockMvc.perform(
                        delete("/films/{id}/like/{userId}", filmId, userId)
                )

                //then
                .andExpect(status().isOk())
                .andExpect(content().string("Пользователь с id 1 удалил лайк у фильма с id 1."));

        mockMvc.perform(
                        get("/films/{id}", filmId)
                )
                .andExpect(jsonPath("$.likes.length()").value(0));
    }

    // Проверка удаление лайка у фильма с неправильным id фильма (ожидается статус 404 Not Found и FilmDoesNotExistException)
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenInvalidFilmIdWhenDeleteLikeThenStatus404andFilmDoesNotExistException() throws Exception {
        //given
        postValidFilm();
        postValidUser();
        Integer filmId = 1;
        Integer userId = 1;
        putValidLike(filmId, userId);
        Integer invalidFilmId = 2;

        //when
        mockMvc.perform(
                        delete("/films/{id}/like/{userId}", invalidFilmId, userId)
                )

                //then
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof FilmDoesNotExistException))
                .andExpect(result -> assertEquals("Фильм c таким ID не существует.",
                        result.getResolvedException().getMessage()));
    }

    // Проверка удаление лайка у фильма с неправильным id пользователя (ожидается статус 404 Not Found и UserDoesNotExistException)
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenInvalidUserIdWhenDeleteLikeThenStatus404andFilmDoesNotExistException() throws Exception {
        //given
        postValidFilm();
        postValidUser();
        Integer filmId = 1;
        Integer userId = 1;
        putValidLike(filmId, userId);
        Integer invalidUserId = 2;

        //when
        mockMvc.perform(
                        delete("/films/{id}/like/{userId}", filmId, invalidUserId)
                )

                //then
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserDoesNotExistException))
                .andExpect(result -> assertEquals("Пользователя c таким ID не существует.",
                        result.getResolvedException().getMessage()));
    }

    // Проверка получения списка самых популярных фильмов
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenValidIdWhenGetPopularThenStatus200andListReturned() throws Exception {
        //given
        postValidFilm();

        //when
        mockMvc.perform(
                        get("/films/popular")
                )

                //then
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Arrays.asList(VALID_FILM))));
    }

    // Проверка получения списка самых популярных фильмов c нулевым count (ожидается статус 400 Bad Request и IncorrectParameterException)
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenZeroCountWhenGetPopularThenStatus400andIncorrectParameterException() throws Exception {
        //given
        postValidFilm();

        //when
        mockMvc.perform(
                        get("/films/popular")
                                .param("count", "0")
                )

                //then
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof IncorrectParameterException))
                .andExpect(result -> assertEquals("Параметр count должен быть положительным.",
                        result.getResolvedException().getMessage()));
    }

    private void postValidFilm() throws Exception {
        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(VALID_FILM))
                        .contentType(MediaType.APPLICATION_JSON)
        );
    }

    private void postValidUser() throws Exception {
        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(VALID_USER))
                        .contentType(MediaType.APPLICATION_JSON)
        );
    }

    private void putValidLike(Integer filmId, Integer userId) throws Exception {
        mockMvc.perform(
                put("/films/{id}/like/{userId}", filmId, userId)
        );
    }
}
