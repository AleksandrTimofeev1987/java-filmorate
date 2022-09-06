package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.exceptions.UserDoesNotExistException;
import ru.yandex.practicum.filmorate.exceptions.UserValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    private static final LocalDate BIRTHDAY = LocalDate.now().minusDays(1);
    private static final LocalDate FUTURE_DATE = LocalDate.now().plusDays(1);
    private static final User VALID_USER = new User(1, "1@yandex.ru", "login", "name", BIRTHDAY);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserController userController;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    public void afterEach() {
        userController.getUsers().clear();
        userController.setId(0);
    }

    // Проверка добавления валидного пользователя
    @Test
    public void userWhenPostThenStatus200andUserReturned() throws Exception {
        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(VALID_USER))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("1@yandex.ru"))
                .andExpect(jsonPath("$.login").value("login"))
                .andExpect(jsonPath("$.name").value("name"));
    }

    // Проверка обновления валидного пользователя
    @Test
    public void userWhenPutThenStatus200andUserReturned() throws Exception {
        postValidUser();

        User updatedUser = new User(1, "12@yandex.ru", "new_login", "new_name", BIRTHDAY);

        mockMvc.perform(
                put("/users")
                        .content(objectMapper.writeValueAsString(updatedUser))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("12@yandex.ru"))
                .andExpect(jsonPath("$.login").value("new_login"))
                .andExpect(jsonPath("$.name").value("new_name"));
    }

    // Проверка добавления валидного пользователя с пустым именем
    @Test
    public void userWhenPostWithBlankNameThenStatus200andUserReturnedWithNameLogin() throws Exception {
        User user = new User(1, "1@yandex.ru", "login", "", BIRTHDAY);

        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("1@yandex.ru"))
                .andExpect(jsonPath("$.login").value("login"))
                .andExpect(jsonPath("$.name").value("login"));
    }

    // Проверка обновления валидного пользователя с пустым именем
    @Test
    public void userWhenPutWithBlankNameThenStatus200andUserReturnedWithNameLogin() throws Exception {
        postValidUser();

        User updatedUser = new User(1, "12@yandex.ru", "new_login", "", BIRTHDAY);

        mockMvc.perform(
                put("/users")
                        .content(objectMapper.writeValueAsString(updatedUser))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("12@yandex.ru"))
                .andExpect(jsonPath("$.login").value("new_login"))
                .andExpect(jsonPath("$.name").value("new_login"));
    }

    // Проверка добавления пользователя с логином, сожержащим пробел (ожидается статус 400 Bad Request и UserValidationException)
    @Test
    public void userWhenPostWithSpaceInLoginThenStatus400andUserValidationException() throws Exception {
        User user = new User(1, "1@yandex.ru", "l ogin", "name", BIRTHDAY);

        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserValidationException))
                .andExpect(result -> assertEquals("Логин не должен содержать пробелы.",
                        result.getResolvedException().getMessage()));
    }

    // Проверка обновления пользователя с логином, сожержащим пробел (ожидается статус 400 Bad Request и UserValidationException)
    @Test
    public void userWhenPutWithSpaceInLoginThenStatus400andUserValidationException() throws Exception {
        postValidUser();

        User updatedUser = new User(1, "1@yandex.ru", "l ogin", "name", BIRTHDAY);

        mockMvc.perform(
                put("/users")
                        .content(objectMapper.writeValueAsString(updatedUser))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserValidationException))
                .andExpect(result -> assertEquals("Логин не должен содержать пробелы.",
                        result.getResolvedException().getMessage()));
    }

    // Проверка обновления пользователя с несуществующим id (ожидается статус 400 Bad Request и UserDoesNotExistException)
    @Test
    public void userWhenPutNonExistentThenStatus404andUserDoesNotExistException() throws Exception {
        postValidUser();

        User updatedUser = new User(2, "1@yandex.ru", "login", "name", BIRTHDAY);

        mockMvc.perform(
                put("/users")
                        .content(objectMapper.writeValueAsString(updatedUser))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserDoesNotExistException))
                .andExpect(result -> assertEquals("Пользователя c таким ID не существует.",
                        result.getResolvedException().getMessage()));
    }

    // Проверка добавления пользователя с пустой электронной почтой (ожидается статус 400 Bad Request)
    @Test
    public void userWhenPostWithBlankEmailThenStatus400() throws Exception {
        User user = new User(1, "", "login", "name", BIRTHDAY);

        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest());
    }

    // Проверка обновления пользователя с пустой электронной почтой (ожидается статус 400 Bad Request)
    @Test
    public void userWhenPutWithBlankEmailThenStatus400() throws Exception {
        postValidUser();

        User updatedUser = new User(1, "", "login", "name", BIRTHDAY);

        mockMvc.perform(
                put("/users")
                        .content(objectMapper.writeValueAsString(updatedUser))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest());
    }

    // Проверка добавления пользователя с некорректной электронной почтой (ожидается статус 400 Bad Request)
    @Test
    public void userWhenPostWithWrongEmailThenStatus400() throws Exception {
        User user = new User(1, "email@", "login", "name", BIRTHDAY);

        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest());
    }

    // Проверка обновления пользователя с некорректной электронной почтой (ожидается статус 400 Bad Request)
    @Test
    public void userWhenPutWithWrongEmailThenStatus400() throws Exception {
        postValidUser();

        User updatedUser = new User(1, "email@", "login", "name", BIRTHDAY);

        mockMvc.perform(
                put("/users")
                        .content(objectMapper.writeValueAsString(updatedUser))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest());
    }

    // Проверка добавления пользователя с пустым логином (ожидается статус 400 Bad Request)
    @Test
    public void userWhenPostWithBlankLoginThenStatus400() throws Exception {
        User user = new User(1, "1@yandex.ru", "", "name", BIRTHDAY);

        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest());
    }

    // Проверка обновления пользователя с пустым логином (ожидается статус 400 Bad Request)
    @Test
    public void userWhenPutWithBlankLoginThenStatus400() throws Exception {
        postValidUser();

        User updatedUser = new User(1, "1@yandex.ru", "", "name", BIRTHDAY);

        mockMvc.perform(
                put("/users")
                        .content(objectMapper.writeValueAsString(updatedUser))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest());
    }

    // Проверка добавления пользователя с днем рождения в будущем (ожидается статус 400 Bad Request)
    @Test
    public void userWhenPostWithBirthdayInFutureLoginThenStatus400() throws Exception {
        User user = new User(1, "1@yandex.ru", "login", "name", FUTURE_DATE);

        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest());
    }

    // Проверка обновления пользователя с днем рождения в будущем (ожидается статус 400 Bad Request)
    @Test
    public void userWhenPutWithBirthdayInFutureThenStatus400() throws Exception {
        postValidUser();

        User updatedUser = new User(1, "1@yandex.ru", "login", "name", FUTURE_DATE);

        mockMvc.perform(
                put("/users")
                        .content(objectMapper.writeValueAsString(updatedUser))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest());
    }

    private void postValidUser() throws Exception {
        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(VALID_USER))
                        .contentType(MediaType.APPLICATION_JSON)
        );
    }
}
