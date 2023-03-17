package ru.yandex.practicum.filmorate.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

@Component
@Slf4j
public class ControllerUtil {

    public void isFilmAndUserExists(FilmService filmService, Integer filmId, Integer userId) {
        if (filmService.getFilm(filmId) == null) {
            log.warn("Ошибка пользователя: Фильм с id-" + filmId + " не найден.");
            throw new FilmNotFoundException("Фильм с id-" + filmId + " не найден.");
        }
        if (!InMemoryUserStorage.isUserExists(userId)) {
            log.warn("Ошибка пользователя: Пользователь с id-" + userId + " не найден.");
            throw new UserNotFoundException("Пользователь с id-" + userId + " не найден.");
        }
    }

    public void isTwoUsersExists(UserService userService, Integer id, Integer friendId) {
        if (userService.getUser(id) == null) {
            log.warn("Ошибка пользователя: Пользователь с id-" + id + " не найден.");
            throw new UserNotFoundException("Пользователь с id-" + id + " не найден.");
        }
        if (userService.getUser(friendId) == null) {
            log.warn("Ошибка пользователя: Друг с id-" + friendId + " не найден.");
            throw new UserNotFoundException("Друг с id-" + friendId + " не найден.");
        }
    }
}
