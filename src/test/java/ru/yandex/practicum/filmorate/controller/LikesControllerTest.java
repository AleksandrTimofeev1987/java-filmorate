package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase()
public class LikesControllerTest {

    private static final LocalDate RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private static final MPA VALID_MPA = new MPA(1, "G");
    private static final Film VALID_FILM = new Film(1, "film", RandomString.make(200), RELEASE_DATE, 1, 0, VALID_MPA);
    private static final LocalDate BIRTHDAY = LocalDate.now().minusDays(1);
    private static final User VALID_USER = new User(1, "1@yandex.ru", "login", "name", BIRTHDAY);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Проверка постановки лайка фильму
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void shouldReturn200OnPutLikeWhenValidFilmIdAndUSerId() throws Exception {
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
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("film"))
                .andExpect(jsonPath("$.releaseDate").value(RELEASE_DATE.toString()))
                .andExpect(jsonPath("$.duration").value(1))
                .andExpect(jsonPath("$.rate").value(1))
                .andExpect(jsonPath("$.likes.length()").value(1))
                .andExpect(jsonPath("$.likes[0]").value(1))
                .andExpect(jsonPath("$.rate").value(1))
                .andExpect(jsonPath("$.mpa.id").value(1))
                .andExpect(jsonPath("$.mpa.name").value("G"));
    }

    // Проверка постановки лайка фильму с неправильным id фильма (ожидается статус 404 Not Found и FilmDoesNotExistException)
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void shouldReturn404OnPutLikeWhenInvalidFilmIdAndUserId() throws Exception {
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
                .andExpect(result -> assertEquals("FilmService: Фильм c таким ID не существует.",
                        result.getResolvedException().getMessage()));
    }

    // Проверка постановки лайка фильму с неправильным id пользователя (ожидается статус 404 Not Found и UserDoesNotExistException)
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void shouldReturn404OnPutLikeWhenInvalidUserId() throws Exception {
        //given
        postValidFilm();
        postValidUser();
        Integer filmId = 1;
        Integer userId = 3;

        //when
        mockMvc.perform(
                        put("/films/{id}/like/{userId}", filmId, userId)
                )

                //then
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserDoesNotExistException))
                .andExpect(result -> assertEquals("UserService: Пользователя c таким ID не существует.",
                        result.getResolvedException().getMessage()));
    }

    // Проверка удаления лайка у фильма
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void shouldReturn200AndFilmOnDeleteLikeWhenValidFilmIdAndUSerId() throws Exception {
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
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("film"))
                .andExpect(jsonPath("$.releaseDate").value(RELEASE_DATE.toString()))
                .andExpect(jsonPath("$.duration").value(1))
                .andExpect(jsonPath("$.rate").value(0))
                .andExpect(jsonPath("$.likes.length()").value(0))
                .andExpect(jsonPath("$.rate").value(0))
                .andExpect(jsonPath("$.mpa.id").value(1))
                .andExpect(jsonPath("$.mpa.name").value("G"));
    }

    // Проверка удаления лайка у фильма, которому пользователь лайк не ставил (ожидается статус 400 Bad Request и IncorrectPathVariableException)
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void shouldReturn400OnDeleteLikeWhenUserDidNotLike() throws Exception {
        //given
        postValidFilm();
        postValidUser();
        Integer filmId = 1;
        Integer userId = 1;

        //when
        mockMvc.perform(
                        delete("/films/{id}/like/{userId}", filmId, userId)
                )

                //then
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof IncorrectPathVariableException))
                .andExpect(result -> assertEquals("LikesService: Пользователь с id 1 попытался удалить лайк у фильма с id 1, которому он не ставил лайк.",
                        result.getResolvedException().getMessage()));
    }

    // Проверка удаление лайка у фильма с неправильным id фильма (ожидается статус 404 Not Found и FilmDoesNotExistException)
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void shouldReturn404OnDeleteLikeWhenInvalidFilmId() throws Exception {
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
                .andExpect(result -> assertEquals("FilmService: Фильм c таким ID не существует.",
                        result.getResolvedException().getMessage()));
    }

    // Проверка удаление лайка у фильма с неправильным id пользователя (ожидается статус 404 Not Found и UserDoesNotExistException)
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void shouldReturn404OnDeleteLikeWhenInvalidUserId() throws Exception {
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
                .andExpect(result -> assertEquals("UserService: Пользователя c таким ID не существует.",
                        result.getResolvedException().getMessage()));
    }

    // Проверка получения списка самых популярных фильмов
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void shouldReturn200andListOnGetPopularWhenValidRequestParam() throws Exception {
        //given
        postValidFilm();
        postValidFilm();

        //when
        mockMvc.perform(
                        get("/films/popular")
                                .param("count", "1")
                )

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(VALID_FILM))));
    }

    // Проверка получения списка самых популярных фильмов без параметра запросов
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void shouldReturn200andListOf10OnGetPopularWhenNoRequestParam() throws Exception {
        //given
        postValidFilm();
        postValidFilm();
        postValidFilm();
        postValidFilm();
        postValidFilm();
        postValidFilm();
        postValidFilm();
        postValidFilm();
        postValidFilm();
        postValidFilm();
        postValidFilm();

        //when
        mockMvc.perform(
                        get("/films/popular")
                )

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(10));
    }

    // Проверка получения списка самых популярных фильмов c нулевым count (ожидается статус 400 Bad Request и IncorrectParameterException)
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void shouldReturn400andListOnGetPopularWhenZeroRequestParam() throws Exception {
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
                .andExpect(result -> assertEquals("LikesController: Параметр count должен быть положительным.",
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
