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
class FilmControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllShouldReturnOk() throws Exception {
        String json = "{" +
                "\"name\": \"Фильм\"," +
                "\"description\": \"Описание\"," +
                "\"releaseDate\": \"2020-05-20\"," +
                "\"duration\": 120" +
                "}";

        mockMvc.perform(post("/films")
                .contentType(APPLICATION_JSON)
                .content(json));
        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("{\"id\":1,\"name\":\"Фильм\"," +
                        "\"description\":\"Описание\",\"releaseDate\":\"2020-05-20\",\"duration\":120}")));
    }

    @Test
    void shouldReturnBadRequestWhenNameIsEmpty() throws Exception {
        String json = "{" +
                "\"description\": \"Короткое описание\"," +
                "\"releaseDate\": \"2020-01-01\"," +
                "\"duration\": 120" +
                "}";

        mockMvc.perform(post("/films")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Название фильма не может быть пустым")));
    }

    @Test
    void shouldReturnBadRequestWhenReleaseDateIsTooOld() throws Exception {
        String json = "{" +
                "\"name\": \"Старый фильм\"," +
                "\"description\": \"Описание\"," +
                "\"releaseDate\": \"1800-01-01\"," +
                "\"duration\": 120" +
                "}";

        mockMvc.perform(post("/films")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Дата релиза не может быть раньше")));
    }

    @Test
    void shouldReturnBadRequestWhenDescriptionTooLong() throws Exception {
        String json = String.format(
                "{" +
                        "\"name\":\"Длинное описание\"," +
                        "\"description\":\"%s\"," +
                        "\"releaseDate\":\"2020-01-01\"," +
                        "\"duration\":120" +
                        "}",
                "A".repeat(201)
        );

        mockMvc.perform(post("/films")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Описание не должно превышать 200 символов")));
    }

    @Test
    void shouldReturnBadRequestWhenDurationIsNotPositive() throws Exception {
        String json = "{" +
                "\"name\": \"Отрицательная продолжительность\"," +
                "\"description\": \"Описание\"," +
                "\"releaseDate\": \"2020-01-01\"," +
                "\"duration\": -120" +
                "}";

        mockMvc.perform(post("/films")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Продолжительность фильма должна быть ")));
    }

    @Test
    void getById_NotFound_returns404() throws Exception {
        mockMvc.perform(get("/films/99999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("{\"error\":\"Фильм с id=99999 не найден\"}"));
    }
}