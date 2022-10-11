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
import ru.yandex.practicum.filmorate.exceptions.IncorrectPathVariableException;
import ru.yandex.practicum.filmorate.exceptions.UserDoesNotExistException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class FriendsControllerTest {

    private static final LocalDate BIRTHDAY = LocalDate.now().minusDays(1);
    private static final User VALID_USER = new User(1, "1@yandex.ru", "login", "name", BIRTHDAY);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Проверка добавления в друзья
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void shouldReturn200AndListOnPutFriendWhenValidUserIdAndFriendId() throws Exception {
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
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].email").value("1@yandex.ru"))
                .andExpect(jsonPath("$[0].login").value("login"))
                .andExpect(jsonPath("$[0].birthday").value(BIRTHDAY.toString()))
                .andExpect(jsonPath("$[0].name").value("name"))
                .andExpect(jsonPath("$[0].friends.length()").value(1))
                .andExpect(jsonPath("$[0].friends[0]").value(2))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].email").value("1@yandex.ru"))
                .andExpect(jsonPath("$[1].login").value("login"))
                .andExpect(jsonPath("$[1].birthday").value(BIRTHDAY.toString()))
                .andExpect(jsonPath("$[1].name").value("name"))
                .andExpect(jsonPath("$[1].friends.length()").value(0));
    }

    // Проверка добавления в друзья по неправильному id (ожидается статус 404 Not Found и UserDoesNotExistException)
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void shouldReturn404OnPutFriendWhenInvalidUserId() throws Exception {
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
    public void shouldReturn404OnPutFriendWhenInvalidFriendId() throws Exception {
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
    public void shouldReturn400OnPutFriendWhenUserIdEqualsFriendId() throws Exception {
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
    public void shouldReturn200AndListOnDeleteFriendWhenValidUserIdAndFriendId() throws Exception {
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
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].email").value("1@yandex.ru"))
                .andExpect(jsonPath("$[0].login").value("login"))
                .andExpect(jsonPath("$[0].birthday").value(BIRTHDAY.toString()))
                .andExpect(jsonPath("$[0].name").value("name"))
                .andExpect(jsonPath("$[0].friends.length()").value(0))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].email").value("1@yandex.ru"))
                .andExpect(jsonPath("$[1].login").value("login"))
                .andExpect(jsonPath("$[1].birthday").value(BIRTHDAY.toString()))
                .andExpect(jsonPath("$[1].name").value("name"))
                .andExpect(jsonPath("$[1].friends.length()").value(0));
    }

    // Проверка удаления из друзей по неправильному id (ожидается статус 404 Not Found и UserDoesNotExistException)
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void shouldReturn404OnDeleteFriendWhenInvalidUserId() throws Exception {
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
    public void shouldReturn404OnDeleteFriendWhenInvalidFriendId() throws Exception {
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
    public void shouldReturn200AndListOnGetFriendsWhenValidUserId() throws Exception {
        //given
        postValidUser();
        postValidUser();
        Integer id = 1;
        Integer friendId = 2;
        putFriend(id, friendId);

        //when
        mockMvc.perform(
                        get("/users/{id}/friends", id)
                )

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("length()").value(1))
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].email").value("1@yandex.ru"))
                .andExpect(jsonPath("$[0].login").value("login"))
                .andExpect(jsonPath("$[0].birthday").value(BIRTHDAY.toString()))
                .andExpect(jsonPath("$[0].name").value("name"))
                .andExpect(jsonPath("$[0].friends.length()").value(0));
    }

    // Проверка получения списка всех друзей по неправильному id (ожидается статус 404 Not Found и UserDoesNotExistException)
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void shouldReturn404OnGetFriendsWhenInvalidUserId() throws Exception {
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
    public void shouldReturn200AndListOnGetCommonFriendsWhenValidUserIdAndOtherId() throws Exception {
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
                .andExpect(jsonPath("$[0].friends.length()").value(0));
    }

    // Проверка получения списка списка общих друзей по неправильному id (ожидается статус 404 Not Found и UserDoesNotExistException)
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void shouldReturn404OnGetCommonFriendsWhenInvalidUserId() throws Exception {
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
    public void shouldReturn404OnGetCommonFriendsWhenInvalidOtherId() throws Exception {
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
