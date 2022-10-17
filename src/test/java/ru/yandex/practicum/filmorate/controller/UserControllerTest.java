package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.exceptions.UserDoesNotExistException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class UserControllerTest {

    private static final LocalDate BIRTHDAY = LocalDate.now().minusDays(1);
    private static final LocalDate FUTURE_DATE = LocalDate.now().plusDays(1);
    private static final User VALID_USER = new User(1, "1@yandex.ru", "login", "name", BIRTHDAY);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Проверка получения списка всех пользователей
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void shouldReturn200andListOnGetAllWhenValidUser() throws Exception {
        //given
        postValidUser();

        //when
        mockMvc.perform(
                        get("/users")
                )

                //then
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(VALID_USER))))
                .andExpect(jsonPath("length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].email").value("1@yandex.ru"))
                .andExpect(jsonPath("$[0].login").value("login"))
                .andExpect(jsonPath("$[0].birthday").value(BIRTHDAY.toString()))
                .andExpect(jsonPath("$[0].name").value("name"))
                .andExpect(jsonPath("$[0].friends.length()").value(0));
    }

    // Проверка добавления валидного пользователя
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void shouldReturn200andUserOnPostUserWhenValidUser() throws Exception {
        //when
        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(VALID_USER))
                                .contentType(MediaType.APPLICATION_JSON)
                )

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("1@yandex.ru"))
                .andExpect(jsonPath("$.login").value("login"))
                .andExpect(jsonPath("$.birthday").value(BIRTHDAY.toString()))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.friends.length()").value(0));
    }

    // Проверка обновления валидного пользователя
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void shouldReturn200andUserOnPutUserWhenValidUser() throws Exception {
        //given
        postValidUser();

        User updatedUser = new User(1, "12@yandex.ru", "new_login", "new_name", BIRTHDAY);

        //when
        mockMvc.perform(
                        put("/users")
                                .content(objectMapper.writeValueAsString(updatedUser))
                                .contentType(MediaType.APPLICATION_JSON)
                )

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("12@yandex.ru"))
                .andExpect(jsonPath("$.login").value("new_login"))
                .andExpect(jsonPath("$.name").value("new_name"))
                .andExpect(jsonPath("$.friends.length()").value(0));
    }

    // Проверка добавления валидного пользователя с пустым именем
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void shouldReturn200andUserWithNameLoginOnPostUserWhenValidUser() throws Exception {
        //given
        User user = new User(1, "1@yandex.ru", "login", "", BIRTHDAY);

        //when
        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON)
                )

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("1@yandex.ru"))
                .andExpect(jsonPath("$.login").value("login"))
                .andExpect(jsonPath("$.name").value("login"))
                .andExpect(jsonPath("$.friends.length()").value(0));
    }

    // Проверка обновления валидного пользователя с пустым именем
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void shouldReturn200andUserWithNameLoginOnPutUserWhenValidUser() throws Exception {
        //given
        postValidUser();

        User updatedUser = new User(1, "12@yandex.ru", "new_login", "", BIRTHDAY);

        //when
        mockMvc.perform(
                        put("/users")
                                .content(objectMapper.writeValueAsString(updatedUser))
                                .contentType(MediaType.APPLICATION_JSON)
                )

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("12@yandex.ru"))
                .andExpect(jsonPath("$.login").value("new_login"))
                .andExpect(jsonPath("$.name").value("new_login"))
                .andExpect(jsonPath("$.friends.length()").value(0));
    }

    // Проверка обновления пользователя с несуществующим id (ожидается статус 404 Not Found и UserDoesNotExistException)
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void shouldReturn404OnPutUserWhenInvalidUserId() throws Exception {
        //given
        postValidUser();

        User updatedUser = new User(2, "1@yandex.ru", "login", "name", BIRTHDAY);

        //when
        mockMvc.perform(
                        put("/users")
                                .content(objectMapper.writeValueAsString(updatedUser))
                                .contentType(MediaType.APPLICATION_JSON)
                )

                //then
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserDoesNotExistException))
                .andExpect(result -> assertEquals("UserService: Пользователя c таким ID не существует.",
                        result.getResolvedException().getMessage()));
    }

    // Проверка добавления пользователя с пустой электронной почтой (ожидается статус 400 Bad Request)
    @Test
    public void shouldReturn400OnPostUserWhenEmptyUserEmail() throws Exception {
        //given
        User user = new User(1, "", "login", "name", BIRTHDAY);

        //when
        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON)
                )

                //then
                .andExpect(status().isBadRequest());
    }

    // Проверка обновления пользователя с пустой электронной почтой (ожидается статус 400 Bad Request)
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void shouldReturn400OnPutUserWhenEmptyUserEmail() throws Exception {
        //given
        postValidUser();

        User updatedUser = new User(1, "", "login", "name", BIRTHDAY);

        //when
        mockMvc.perform(
                        put("/users")
                                .content(objectMapper.writeValueAsString(updatedUser))
                                .contentType(MediaType.APPLICATION_JSON)
                )

                //then
                .andExpect(status().isBadRequest());
    }

    // Проверка добавления пользователя с некорректной электронной почтой (ожидается статус 400 Bad Request)
    @Test
    public void shouldReturn400OnPostUserWhenInvalidUserEmail() throws Exception {
        //given
        User user = new User(1, "email@", "login", "name", BIRTHDAY);

        //when
        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON)
                )

                //then
                .andExpect(status().isBadRequest());
    }

    // Проверка обновления пользователя с некорректной электронной почтой (ожидается статус 400 Bad Request)
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void shouldReturn400OnPutUserWhenInvalidUserEmail() throws Exception {
        //given
        postValidUser();

        User updatedUser = new User(1, "email@", "login", "name", BIRTHDAY);

        //when
        mockMvc.perform(
                        put("/users")
                                .content(objectMapper.writeValueAsString(updatedUser))
                                .contentType(MediaType.APPLICATION_JSON)
                )

                //then
                .andExpect(status().isBadRequest());
    }

    // Проверка добавления пользователя с пустым логином (ожидается статус 400 Bad Request)
    @Test
    public void shouldReturn400OnPostUserWhenEmptyUserLogin() throws Exception {
        //given
        User user = new User(1, "1@yandex.ru", "", "name", BIRTHDAY);

        //when
        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON)
                )

                //then
                .andExpect(status().isBadRequest());
    }

    // Проверка обновления пользователя с пустым логином (ожидается статус 400 Bad Request)
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void shouldReturn400OnPutUserWhenEmptyUserLogin() throws Exception {
        //given
        postValidUser();

        User updatedUser = new User(1, "1@yandex.ru", "", "name", BIRTHDAY);

        //when
        mockMvc.perform(
                        put("/users")
                                .content(objectMapper.writeValueAsString(updatedUser))
                                .contentType(MediaType.APPLICATION_JSON)
                )

                //then
                .andExpect(status().isBadRequest());
    }

    // Проверка добавления пользователя с логином, сожержащим пробел (ожидается статус 400 Bad Request)
    @Test
    public void shouldReturn400OnPostUserWhenUserLoginWithSpace() throws Exception {
        //given
        User user = new User(1, "1@yandex.ru", "l ogin", "name", BIRTHDAY);

        //when
        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON)
                )

                //then
                .andExpect(status().isBadRequest());
    }

    // Проверка обновления пользователя с логином, сожержащим пробел (ожидается статус 400 Bad Request)
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void shouldReturn400OnPutUserWhenUserLoginWithSpace() throws Exception {
        //given
        postValidUser();

        User updatedUser = new User(1, "1@yandex.ru", "l ogin", "name", BIRTHDAY);

        //when
        mockMvc.perform(
                        put("/users")
                                .content(objectMapper.writeValueAsString(updatedUser))
                                .contentType(MediaType.APPLICATION_JSON)
                )

                //then
                .andExpect(status().isBadRequest());
    }

    // Проверка добавления пользователя с днем рождения в будущем (ожидается статус 400 Bad Request)
    @Test
    public void shouldReturn400OnPostUserWhenFutureUserBirthday() throws Exception {
        //given
        User user = new User(1, "1@yandex.ru", "login", "name", FUTURE_DATE);

        //when
        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON)
                )

                //then
                .andExpect(status().isBadRequest());
    }

    // Проверка обновления пользователя с днем рождения в будущем (ожидается статус 400 Bad Request)
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void shouldReturn400OnPutUserWhenFutureUserBirthday() throws Exception {
        //given
        postValidUser();

        User updatedUser = new User(1, "1@yandex.ru", "login", "name", FUTURE_DATE);

        //when
        mockMvc.perform(
                        put("/users")
                                .content(objectMapper.writeValueAsString(updatedUser))
                                .contentType(MediaType.APPLICATION_JSON)
                )

                //then
                .andExpect(status().isBadRequest());
    }

    // Проверка получения пользователя по валидному id
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void shouldReturn200AndUserOnGetUserWhenValidUserId() throws Exception {
        //given
        postValidUser();
        Integer id = 1;

        //when
        mockMvc.perform(
                        get("/users/{id}", id)
                )

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("1@yandex.ru"))
                .andExpect(jsonPath("$.login").value("login"))
                .andExpect(jsonPath("$.birthday").value(BIRTHDAY.toString()))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.friends.length()").value(0));
    }

    // Проверка получения пользователя по неправильному id (ожидается статус 404 Not Found и UserDoesNotExistException)
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void shouldReturn404OnGetUserWhenInvalidUserId() throws Exception {
        //given
        postValidUser();
        Integer id = 2;

        //when
        mockMvc.perform(
                        get("/users/{id}", id)
                )

                //then
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserDoesNotExistException))
                .andExpect(result -> assertEquals("UserService: Пользователя c таким ID не существует.",
                        result.getResolvedException().getMessage()));
    }

    // Проверка удаления пользователя по валидному id
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void shouldReturn200AndUserOnDeleteUserWhenValidUserId() throws Exception {
        //given
        postValidUser();
        Integer id = 1;

        //when
        mockMvc.perform(
                        delete("/users/{id}", id)
                )

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("1@yandex.ru"))
                .andExpect(jsonPath("$.login").value("login"))
                .andExpect(jsonPath("$.birthday").value(BIRTHDAY.toString()))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.friends.length()").value(0));

        mockMvc.perform(
                        get("/users")
                )
                .andExpect(jsonPath("length()").value(0));
    }

    // Проверка удаления пользователя по неправильному id (ожидается статус 404 Not Found и UserDoesNotExistException)
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void shouldReturn404OnDeleteUserWhenInvalidUserId() throws Exception {
        //given
        postValidUser();
        Integer id = 2;

        //when
        mockMvc.perform(
                        delete("/users/{id}", id)
                )

                //then
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserDoesNotExistException))
                .andExpect(result -> assertEquals("UserService: Пользователя c таким ID не существует.",
                        result.getResolvedException().getMessage()));

        mockMvc.perform(
                        get("/users")
                )
                .andExpect(jsonPath("length()").value(1));
    }

    private void postValidUser() throws Exception {
        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(VALID_USER))
                        .contentType(MediaType.APPLICATION_JSON)
        );
    }
}
