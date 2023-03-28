package ru.yandex.practicum.filmorate.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

@Component
@Slf4j
public class ControllerUtil {
    private final UserStorage userStorage;

    public ControllerUtil(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void isFilmAndUserExists(FilmStorage filmStorage, Integer filmId, Integer userId) {
        if (filmStorage.getFilm(filmId) == null) {
            log.warn("Ошибка пользователя: Фильм с id-" + filmId + " не найден.");
            throw new FilmNotFoundException("Фильм с id-" + filmId + " не найден.");
        }
        if (userStorage instanceof UserDbStorage && userStorage.getUser(userId) == null) {
            userError(userId);
        } else if (userStorage instanceof InMemoryUserStorage && !InMemoryUserStorage.isUserExists(userId)) {
            userError(userId);
        }
    }

    public void isTwoUsersExists(UserService userService, Integer id, Integer friendId) {
        if (userService.getUser(id) == null) {
            userError(id);
        }
        if (userService.getUser(friendId) == null) {
            log.warn("Ошибка пользователя: Друг с id-" + friendId + " не найден.");
            throw new UserNotFoundException("Друг с id-" + friendId + " не найден.");
        }
    }

    private void userError(Integer userId) {
        log.warn("Ошибка пользователя: Пользователь с id-" + userId + " не найден.");
        throw new UserNotFoundException("Пользователь с id-" + userId + " не найден.");
    }
}
