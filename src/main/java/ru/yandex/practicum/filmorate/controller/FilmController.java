package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.store.FilmStore;

import java.util.Collection;

@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {
    private final FilmStore filmStore;

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Поступил POST-запрос на создание фильма: name={}, description={}",
                film.getName(), film.getDescription());
        Film createdFilm = filmStore.add(film);
        log.info("Фильм успешно создан: name={}, description={}", createdFilm.getName(), createdFilm.getDescription());
        return createdFilm;
    }

    @GetMapping
    public Collection<Film> getAll() {
        log.debug("Поступил GET-запрос: получение всех фильмов");
        return filmStore.getAll();
    }

    @GetMapping("/{id}")
    public Film getById(@PathVariable Long id) {
        log.debug("Поступил GET-запрос для фильма с id={}", id);
        Film film = filmStore.getById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + id + " не найден"));
        log.info("Возвращаем фильм: id={}, name={}", film.getId(), film.getName());
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Поступил PUT-запрос на изменение фильма: id={}, name={}", film.getId(), film.getName());
        if (film.getId() == null) {
            log.warn("Попытка обновления фильма без ID в теле запроса");
            throw new BadRequestException("ID фильма должен быть указан в теле запроса");
        }
        filmStore.getById(film.getId())
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + film.getId() + " не найден"));
        log.info("Фильм с id={} успешно изменен", film.getId());
        return filmStore.update(film);
    }
}
