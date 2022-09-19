package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.exceptions.IncorrectPathVariableException;
import ru.yandex.practicum.filmorate.exceptions.UserDoesNotExistException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    private ObjectMapper objectMapper;

    // Проверка получения списка всех пользователей
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenValidUserWhenGetAllThenStatus200andListReturned() throws Exception {
        //given
        postValidUser();

        //when
        mockMvc.perform(
                        get("/users")
                )

                //then
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Arrays.asList(VALID_USER))))
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
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.friends.length()").value(0));
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
                .andExpect(jsonPath("$.name").value("new_name"))
                .andExpect(jsonPath("$.friends.length()").value(0));
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
                .andExpect(jsonPath("$.name").value("login"))
                .andExpect(jsonPath("$.friends.length()").value(0));
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
                .andExpect(jsonPath("$.name").value("new_login"))
                .andExpect(jsonPath("$.friends.length()").value(0));
    }

    // Проверка обновления пользователя с несуществующим id (ожидается статус 404 Not Found и UserDoesNotExistException)
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

    // Проверка получения пользователя по валидному id
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenValidIdWhenGetThenStatus200andUserReturned() throws Exception {
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
    public void givenInvalidIdWhenGetThenStatus404andUserDoesNotExistException() throws Exception {
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
                .andExpect(result -> assertEquals("Пользователя c таким ID не существует.",
                        result.getResolvedException().getMessage()));
    }

    // Проверка добавления в друзья
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenValidUserWhenPutFriendThenStatus200andMessage() throws Exception {
        //given
        postValidUser();
        postValidUser();
        Integer id = 1;
        Integer friendId = 2;

        //when
        mockMvc.perform(
                        put("/users/{id}/friends/{friendId}", id, friendId)
                )

                //then
                .andExpect(status().isOk())
                .andExpect(content().string("Пользователь с id 1 стал другом пользователя с id 2."));

        mockMvc.perform(
                get("/users/{id}", id)
        )
                .andExpect(jsonPath("$.friends.length()").value(1))
                .andExpect(jsonPath("$.friends[0]").value("2"));
    }

    // Проверка добавления в друзья по неправильному id (ожидается статус 404 Not Found и UserDoesNotExistException)
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenValidUserWhenPutFriendWrongIdThenStatus404andUserDoesNotExistException() throws Exception {
        //given
        postValidUser();
        postValidUser();
        Integer id = 3;
        Integer friendId = 2;

        //when
        mockMvc.perform(
                        put("/users/{id}/friends/{friendId}", id, friendId)
                )

                //then
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserDoesNotExistException))
                .andExpect(result -> assertEquals("Пользователя c таким ID не существует.",
                        result.getResolvedException().getMessage()));
    }

    // Проверка добавления в друзья по неправильному id друга (ожидается статус 404 Not Found и UserDoesNotExistException)
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenValidUserWhenPutFriendWrongFriendIdThenStatus404andUserDoesNotExistException() throws Exception {
        //given
        postValidUser();
        postValidUser();
        Integer id = 1;
        Integer friendId = 3;

        //when
        mockMvc.perform(
                        put("/users/{id}/friends/{friendId}", id, friendId)
                )

                //then
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserDoesNotExistException))
                .andExpect(result -> assertEquals("Пользователя c таким ID не существует.",
                        result.getResolvedException().getMessage()));
    }

    // Проверка добавления в друзья себя (ожидается статус 400 Bad Request и IncorrectPathVariableException)
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenValidUserWhenPutFriendHimselfThenStatus400andIncorrectPathVariableException() throws Exception {
        //given
        postValidUser();
        Integer id = 1;

        //when
        mockMvc.perform(
                        put("/users/{id}/friends/{friendId}", id, id)
                )

                //then
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof IncorrectPathVariableException))
                .andExpect(result -> assertEquals("Нельзя добавить себя в друзья.",
                        result.getResolvedException().getMessage()));
    }

    // Проверка удаления из друзей
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenValidUserWhenDeleteFriendThenStatus200andMessage() throws Exception {
        //given
        postValidUser();
        postValidUser();
        Integer id = 1;
        Integer friendId = 2;
        putFriend(id, friendId);

        //when
        mockMvc.perform(
                        delete("/users/{id}/friends/{friendId}", id, friendId)
                )

                //then
                .andExpect(status().isOk())
                .andExpect(content().string("Пользователь с id 1 удалил из друзей пользователя с id 2."));

        mockMvc.perform(
                        get("/users/{id}", id)
                )
                .andExpect(jsonPath("$.friends.length()").value(0));
    }

    // Проверка удаления из друзей по неправильному id (ожидается статус 404 Not Found и UserDoesNotExistException)
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenValidUserWhenDeleteFriendWrongIdThenStatus404andUserDoesNotExistException() throws Exception {
        //given
        postValidUser();
        postValidUser();
        Integer id = 1;
        Integer friendId = 2;
        putFriend(id, friendId);
        Integer wrongId = 3;

        //when
        mockMvc.perform(
                        delete("/users/{id}/friends/{friendId}", wrongId, friendId)
                )

                //then
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserDoesNotExistException))
                .andExpect(result -> assertEquals("Пользователя c таким ID не существует.",
                        result.getResolvedException().getMessage()));
    }

    // Проверка удаления из друзей по неправильному id друга (ожидается статус 404 Not Found и UserDoesNotExistException)
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenValidUserWhenDeleteFriendWrongFriendIdThenStatus404andUserDoesNotExistException() throws Exception {
        //given
        postValidUser();
        postValidUser();
        Integer id = 1;
        Integer friendId = 2;
        putFriend(id, friendId);
        Integer wrongId = 3;

        //when
        mockMvc.perform(
                        delete("/users/{id}/friends/{friendId}", id, wrongId)
                )

                //then
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserDoesNotExistException))
                .andExpect(result -> assertEquals("Пользователя c таким ID не существует.",
                        result.getResolvedException().getMessage()));
    }

    // Проверка получения списка всех друзей
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenValidUserWhenGetFriendsThenStatus200andListReturned() throws Exception {
        //given
        postValidUser();
        postValidUser();
        Integer id = 1;
        Integer friendId = 2;
        putFriend(id, friendId);

        //when
        mockMvc.perform(
                        get("/users/{id}/friends", friendId)
                )

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].email").value("1@yandex.ru"))
                .andExpect(jsonPath("$[0].login").value("login"))
                .andExpect(jsonPath("$[0].birthday").value(BIRTHDAY.toString()))
                .andExpect(jsonPath("$[0].name").value("name"))
                .andExpect(jsonPath("$[0].friends[0]").value("2"))
                .andExpect(jsonPath("$[0].friends.length()").value(1));
    }

    // Проверка получения списка всех друзей по неправильному id (ожидается статус 404 Not Found и UserDoesNotExistException)
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenValidUserWhenGetFriendsWrongIdThenStatus404andUserDoesNotExistException() throws Exception {
        //given
        postValidUser();
        postValidUser();
        Integer id = 1;
        Integer friendId = 3;
        putFriend(id, friendId);

        //when
        mockMvc.perform(
                        get("/users/{id}/friends", friendId)
                )

                //then
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserDoesNotExistException))
                .andExpect(result -> assertEquals("Пользователя c таким ID не существует.",
                        result.getResolvedException().getMessage()));
    }

    // Проверка получения списка общих друзей
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenValidUserWhenGetCommonFriendsThenStatus200andListReturned() throws Exception {
        //given
        postValidUser();
        postValidUser();
        postValidUser();
        Integer id = 1;
        Integer friendId = 2;
        Integer commonFriendId = 3;
        putFriend(id, commonFriendId);
        putFriend(friendId, commonFriendId);

        //when
        mockMvc.perform(
                        get("/users/{id}/friends/common/{otherId}", id, friendId)
                )

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("length()").value(1))
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[0].email").value("1@yandex.ru"))
                .andExpect(jsonPath("$[0].login").value("login"))
                .andExpect(jsonPath("$[0].birthday").value(BIRTHDAY.toString()))
                .andExpect(jsonPath("$[0].name").value("name"))
                .andExpect(jsonPath("$[0].friends[0]").value("1"))
                .andExpect(jsonPath("$[0].friends[1]").value("2"))
                .andExpect(jsonPath("$[0].friends.length()").value("2"));
    }

    // Проверка получения списка списка общих друзей по неправильному id (ожидается статус 404 Not Found и UserDoesNotExistException)
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenValidUserWhenGetCommonWrongIdThenStatus404andUserDoesNotExistException() throws Exception {
        //given
        postValidUser();
        postValidUser();
        postValidUser();
        Integer id = 1;
        Integer friendId = 2;
        Integer commonFriendId = 3;
        putFriend(id, friendId);
        putFriend(friendId, commonFriendId);
        Integer wrongId = 4;

        //when
        mockMvc.perform(
                        get("/users/{id}/friends/common/{otherId}", wrongId, friendId)
                )

                //then
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserDoesNotExistException))
                .andExpect(result -> assertEquals("Пользователя c таким ID не существует.",
                        result.getResolvedException().getMessage()));
    }

    // Проверка получения списка списка общих друзей по неправильному id (ожидается статус 404 Not Found и UserDoesNotExistException)
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenValidUserWhenGetCommonWrongFriendIdThenStatus404andUserDoesNotExistException() throws Exception {
        //given
        postValidUser();
        postValidUser();
        postValidUser();
        Integer id = 1;
        Integer friendId = 2;
        Integer commonFriendId = 3;
        putFriend(id, friendId);
        putFriend(friendId, commonFriendId);
        Integer wrongId = 4;

        //when
        mockMvc.perform(
                        get("/users/{id}/friends/common/{otherId}", id, wrongId)
                )

                //then
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserDoesNotExistException))
                .andExpect(result -> assertEquals("Пользователя c таким ID не существует.",
                        result.getResolvedException().getMessage()));
    }

    private void postValidUser() throws Exception {
        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(VALID_USER))
                        .contentType(MediaType.APPLICATION_JSON)
        );
    }

    private void putFriend(Integer id, Integer friendId) throws Exception {
        mockMvc.perform(
                put("/users/{id}/friends/{friendId}", id, friendId)
        );
    }
}
