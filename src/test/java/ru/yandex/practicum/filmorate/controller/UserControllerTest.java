package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllShouldReturnOk() throws Exception {
        String json = "{" +
                "\"email\": \"test@ex.com\"," +
                "\"login\": \"лог\"," +
                "\"name\": \"Тест\"," +
                "\"birthday\": \"2000-12-12\"" +
                "}";

        mockMvc.perform(post("/users")
                .contentType(APPLICATION_JSON)
                .content(json));
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("{\"id\":1,\"email\":\"test@ex.com\"," +
                        "\"login\":\"лог\",\"name\":\"Тест\",\"birthday\":\"2000-12-12\"}")));
    }

    @Test
    void shouldReturnBadRequestWhenLoginIsEmpty() throws Exception {
        String json = "{" +
                "\"email\": \"test@example.com\"," +
                "\"name\": \"Тестовый пользователь\"," +
                "\"birthday\": \"2000-12-12\"" +
                "}";

        mockMvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Логин не может быть пустым")));
    }

    @Test
    void shouldReturnBadRequestWhenLoginHasWhitespaces() throws Exception {
        String json = "{" +
                "\"email\": \"test@example.com\"," +
                "\"login\": \"Тестовый логин\"," +
                "\"name\": \"Тестовый пользователь\"," +
                "\"birthday\": \"2000-12-12\"" +
                "}";

        mockMvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Логин не может содержать пробелы")));
    }

    @Test
    void shouldReturnBadRequestWhenLoginIsSpaces() throws Exception {
        String json = "{" +
                "\"login\": \"   \"," +
                "\"email\": \"test@example.com\"," +
                "\"name\": \"Тестовый пользователь\"," +
                "\"birthday\": \"2000-12-12\"" +
                "}";

        mockMvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Логин не может быть пустым")));
    }

    @Test
    void shouldReturnBadRequestWhenEmailIsEmpty() throws Exception {
        String json = "{" +
                "\"login\": \"логин\"," +
                "\"name\": \"Тестовый пользователь\"," +
                "\"birthday\": \"2000-12-12\"" +
                "}";

        mockMvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Электронная почта не может быть пустой")));
    }

    @Test
    void shouldReturnBadRequestWhenEmailIsInvalid() throws Exception {
        String json = "{" +
                "\"email\": \"почта???\"," +
                "\"login\": \"логин\"," +
                "\"name\": \"Тестовый пользователь\"," +
                "\"birthday\": \"2000-12-12\"" +
                "}";

        mockMvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Некорректный формат электронной почты")));
    }

    @Test
    void shouldReturnBadRequestWhenBirthdayIsEmpty() throws Exception {
        String json = "{" +
                "\"email\": \"test@example.com\"," +
                "\"login\": \"логин\"," +
                "\"name\": \"Тестовый пользователь\"" +
                "}";

        mockMvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Дата рождения обязательна")));
    }

    @Test
    void shouldReturnBadRequestWhenBirthdayIsInFuture() throws Exception {
        String json = "{" +
                "\"email\": \"test@example.com\"," +
                "\"login\": \"логин\"," +
                "\"name\": \"Тестовый пользователь\"," +
                "\"birthday\": \"9000-12-12\"" +
                "}";

        mockMvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Дата рождения не может быть в будущем")));
    }

    @Test
    void getById_NotFound_returns404() throws Exception {
        mockMvc.perform(get("/users/99999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("{\"error\":\"Пользователь с id=99999 не найден\"}"));
    }
}