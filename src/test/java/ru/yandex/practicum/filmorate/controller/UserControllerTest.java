package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.exceptions.UserDoesNotExistException;
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

    // Проверка добавления валидного пользователя
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenValidUserWhenPostThenStatus200andUserReturned() throws Exception {
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
                .andExpect(jsonPath("$.name").value("name"));
    }

    // Проверка обновления валидного пользователя
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenValidUserWhenPutThenStatus200andUserReturned() throws Exception {
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
                .andExpect(jsonPath("$.name").value("new_name"));
    }

    // Проверка добавления валидного пользователя с пустым именем
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenUserWithBlankNameWhenPostThenStatus200andUserReturnedWithNameLogin() throws Exception {
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
                .andExpect(jsonPath("$.name").value("login"));
    }

    // Проверка обновления валидного пользователя с пустым именем
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenUserWithBlankNameWhenPutThenStatus200andUserReturnedWithNameLogin() throws Exception {
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
                .andExpect(jsonPath("$.name").value("new_login"));
    }

    // Проверка обновления пользователя с несуществующим id (ожидается статус 400 Bad Request и UserDoesNotExistException)
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenNonExistentUserWhenPutThenStatus404andUserDoesNotExistException() throws Exception {
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
                .andExpect(result -> assertEquals("Пользователя c таким ID не существует.",
                        result.getResolvedException().getMessage()));
    }

    // Проверка добавления пользователя с пустой электронной почтой (ожидается статус 400 Bad Request)
    @Test
    public void givenUserWithBlankEmailWhenPostThenStatus400() throws Exception {
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
    public void givenUserWithBlankEmailWhenPutThenStatus400() throws Exception {
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
    public void givenUserWithWrongEmailWhenPostThenStatus400() throws Exception {
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
    public void givenUserWithWrongEmailWhenPutThenStatus400() throws Exception {
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
    public void givenUserWithBlankLoginWhenPostThenStatus400() throws Exception {
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
    public void givenUserWithBlankLoginWhenPutThenStatus400() throws Exception {
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
    public void givenUserWithSpaceInLoginWhenPostThenStatus400() throws Exception {
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
    public void givenUserWithSpaceInLoginWhenPutThenStatus400() throws Exception {
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
    public void givenUserWithBirthdayInFutureWhenPostThenStatus400() throws Exception {
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
    public void givenUserWithBirthdayInFutureWhenPutThenStatus400() throws Exception {
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

    private void postValidUser() throws Exception {
        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(VALID_USER))
                        .contentType(MediaType.APPLICATION_JSON)
        );
    }
}
