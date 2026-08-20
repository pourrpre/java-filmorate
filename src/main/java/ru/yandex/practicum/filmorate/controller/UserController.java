package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.store.UserStore;

import java.util.Collection;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserStore userStore;

    public UserController(UserStore userStore) {
        this.userStore = userStore;
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Поступил POST-запрос на создание пользователя: login={}, email={}",
                user.getLogin(), user.getEmail());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Поле name пустое, установлено значение из login: {}", user.getName());
        }
        User createdUser = userStore.add(user);
        log.info("Пользователь успешно создан: id={}, name={}", createdUser.getId(), createdUser.getName());
        return createdUser;
    }

    @GetMapping
    public Collection<User> getAll() {
        log.debug("Поступил GET-запрос: получение всех пользователей");
        return userStore.getAll();
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable Long id) {
        log.debug("Поступил GET-запрос для пользователя с id={}", id);
        User user = userStore.getById(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с id=" + id + " не найден");
        }
        log.info("Возвращаем пользователя: id={}, login={}", user.getId(), user.getLogin());
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Поступил PUT-запрос на изменение пользователя: id={}, name={}", user.getId(), user.getName());
        if (user.getId() == null) {
            log.warn("Попытка обновления пользователя без ID в теле запроса");
            throw new BadRequestException("ID пользователя должен быть указан в теле запроса");
        }
        if (userStore.getById(user.getId()) == null) {
            log.warn("Попытка обновления несуществующего пользователя: id={}", user.getId());
            throw new NotFoundException("Пользователь с id=" + user.getId() + " не найден");
        }
        log.info("Пользователь с id={} успешно изменен", user.getId());
        return userStore.update(user);
    }
}
