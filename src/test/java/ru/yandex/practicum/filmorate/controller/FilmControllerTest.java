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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class FilmControllerTest {

    private static final LocalDate RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private static final LocalDate FUTURE_DATE = LocalDate.now().plusDays(1);
    private static final MPA VALID_MPA = new MPA(1, "G");
    private static final Film VALID_FILM = new Film(1, "film", RandomString.make(200), RELEASE_DATE, 1, 0, VALID_MPA);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Проверка получения списка всех фильмов
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void shouldReturn200andListOnGetAllWhenValidFilm() throws Exception {
        //given
        postValidFilm();

        //when
        mockMvc.perform(
                        get("/films")
                )

                //then
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(VALID_FILM))))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("film"))
                .andExpect(jsonPath("$[0].releaseDate").value(RELEASE_DATE.toString()))
                .andExpect(jsonPath("$[0].duration").value(1))
                .andExpect(jsonPath("$[0].rate").value(0))
                .andExpect(jsonPath("$[0].likes.length()").value(0))
                .andExpect(jsonPath("$[0].rate").value(0))
                .andExpect(jsonPath("$[0].mpa.id").value(1))
                .andExpect(jsonPath("$[0].mpa.name").value("G"));
    }

    // Проверка добавления валидного фильма
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void shouldReturn200andFilmOnPostFilmWhenValidFilm() throws Exception {
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
                .andExpect(jsonPath("$.rate").value(0))
                .andExpect(jsonPath("$.likes.length()").value(0))
                .andExpect(jsonPath("$.rate").value(0))
                .andExpect(jsonPath("$.mpa.id").value(1))
                .andExpect(jsonPath("$.mpa.name").value("G"));
    }

    // Проверка обновления валидного фильма
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void shouldReturn200andFilmOnPutFilmWhenValidFilm() throws Exception {
        //given
        postValidFilm();

        Film updatedFilm = new Film(1, "updated film", RandomString.make(200), RELEASE_DATE, 2,0, VALID_MPA);

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
                .andExpect(jsonPath("$.rate").value(0))
                .andExpect(jsonPath("$.likes.length()").value(0))
                .andExpect(jsonPath("$.rate").value(0))
                .andExpect(jsonPath("$.mpa.id").value(1))
                .andExpect(jsonPath("$.mpa.name").value("G"));
    }

    // Проверка добавления фильма с датой релиза ранее 28 декабря 1895 года (ожидается статус 400 Bad Request и FilmValidationException)
    @Test
    public void shouldReturn400OnPostFilmWhenInvalidFilmRelease() throws Exception {
        //given
        Film film = new Film(1, "film", RandomString.make(200), RELEASE_DATE.minusDays(1), 1,0, VALID_MPA);

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
    public void shouldReturn400OnPutFilmWhenInvalidFilmRelease() throws Exception {
        //given
        postValidFilm();

        Film updatedFilm = new Film(1, "film", RandomString.make(200), RELEASE_DATE.minusDays(1), 1,0, VALID_MPA);

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
    public void shouldReturn404OnPutFilmWhenInvalidFilmId() throws Exception {
        //given
        postValidFilm();

        Film updatedFilm = new Film(2, "updated film", RandomString.make(200), RELEASE_DATE, 1,0, VALID_MPA);

        //when
        mockMvc.perform(
                        put("/films")
                                .content(objectMapper.writeValueAsString(updatedFilm))
                                .contentType(MediaType.APPLICATION_JSON)
                )

                //then
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof FilmDoesNotExistException))
                .andExpect(result -> assertEquals("FilmService: Фильм c таким ID не существует.",
                        result.getResolvedException().getMessage()));
    }

    // Проверка добавления фильма с пустым названием (ожидается статус 400 Bad Request)
    @Test
    public void shouldReturn400OnPostFilmWhenBlankFilmName() throws Exception {
        //given
        Film film = new Film(1, "", RandomString.make(200), RELEASE_DATE, 1,0, VALID_MPA);

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
    public void shouldReturn400OnPutFilmWhenBlankFilmName() throws Exception {
        //given
        postValidFilm();

        Film updatedFilm = new Film(1, "", RandomString.make(200), RELEASE_DATE, 1,0, VALID_MPA);

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
    public void shouldReturn400OnPostFilmWhenLongFilmDescription() throws Exception {
        //given
        Film film = new Film(1, "film", RandomString.make(201), RELEASE_DATE, 1,0, VALID_MPA);

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
    public void shouldReturn400OnPutFilmWhenLongFilmDescription() throws Exception {
        //given
        postValidFilm();

        Film updatedFilm = new Film(1, "film", RandomString.make(201), RELEASE_DATE, 1,0, VALID_MPA);

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
    public void shouldReturn400OnPostFilmWhenFutureFilmRelease() throws Exception {
        //given
        Film film = new Film(1, "film", RandomString.make(200), FUTURE_DATE, 1,0, VALID_MPA);

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
    public void shouldReturn400OnPutFilmWhenFutureFilmRelease() throws Exception {
        //given
        postValidFilm();

        Film updatedFilm = new Film(1, "film", RandomString.make(200), FUTURE_DATE, 1,0, VALID_MPA);

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
    public void shouldReturn400OnPostFilmWhenNegativeFilmDuration() throws Exception {
        //given
        Film film = new Film(1, "film", RandomString.make(200), RELEASE_DATE, -1,0, VALID_MPA);

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
    public void shouldReturn400OnPutFilmWhenNegativeFilmDuration() throws Exception {
        //given
        postValidFilm();

        Film updatedFilm = new Film(1, "film", RandomString.make(200), RELEASE_DATE, -1,0, VALID_MPA);

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
    public void shouldReturn400OnPostFilmWhenZeroFilmDuration() throws Exception {
        //given
        Film film = new Film(1, "film", RandomString.make(200), RELEASE_DATE, 0,0, VALID_MPA);

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
    public void shouldReturn400OnPutFilmWhenZeroFilmDuration() throws Exception {
        //given
        postValidFilm();

        Film updatedFilm = new Film(1, "film", RandomString.make(200), RELEASE_DATE, 0,0, VALID_MPA);

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
    public void shouldReturn200AndFilmOnGetFilmWhenValidFilm() throws Exception {
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
                .andExpect(jsonPath("$.rate").value(0))
                .andExpect(jsonPath("$.likes.length()").value(0))
                .andExpect(jsonPath("$.rate").value(0))
                .andExpect(jsonPath("$.mpa.id").value(1))
                .andExpect(jsonPath("$.mpa.name").value("G"));
    }

    // Проверка получения фильма по неправильному id (ожидается статус 404 Not Found и FilmDoesNotExistException)
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void shouldReturn404OnGetFilmWhenInvalidFilmId() throws Exception {
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
                .andExpect(result -> assertEquals("FilmService: Фильм c таким ID не существует.",
                        result.getResolvedException().getMessage()));
    }

    // Проверка удаления фильма по валидному id
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void shouldReturn200AndFilmOnDeleteFilmWhenValidFilm() throws Exception {
        //given
        postValidFilm();
        Integer id = 1;

        //when
        mockMvc.perform(
                        delete("/films/{id}", id)
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

        mockMvc.perform(
                get("/films")
        )
                .andExpect(jsonPath("$.length()").value(0));
    }

    // Проверка удаления фильма по неправильному id (ожидается статус 404 Not Found и FilmDoesNotExistException)
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void shouldReturn404OnDeleteFilmWhenInvalidFilmId() throws Exception {
        //given
        postValidFilm();
        Integer id = 2;

        //when
        mockMvc.perform(
                        delete("/films/{id}", id)
                )

                //then
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof FilmDoesNotExistException))
                .andExpect(result -> assertEquals("FilmService: Фильм c таким ID не существует.",
                        result.getResolvedException().getMessage()));

        mockMvc.perform(
                        get("/films")
                )
                .andExpect(jsonPath("$.length()").value(1));
    }

    private void postValidFilm() throws Exception {
        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(VALID_FILM))
                        .contentType(MediaType.APPLICATION_JSON)
        );
    }
}
