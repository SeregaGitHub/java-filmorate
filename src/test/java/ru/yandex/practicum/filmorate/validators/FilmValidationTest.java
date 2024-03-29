package ru.yandex.practicum.filmorate.validators;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controllers.FilmController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.film_util.LikeStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.util.ControllerUtil;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

public class FilmValidationTest {
    private Film film;
    private FilmController filmController;
    private FilmValidator filmValidator;
    private FilmService filmService;
    private FilmStorage filmStorage;
    private UserStorage userStorage;
    private ControllerUtil controllerUtil;
    private LikeStorage likeStorage;
    private Mpa mpa;

    @BeforeEach
    void beforeEach() {
        controllerUtil = new ControllerUtil(userStorage);
        userStorage = new InMemoryUserStorage();
        filmValidator = new FilmValidator();
        filmStorage = new InMemoryFilmStorage();
        filmService = new FilmService(filmStorage, controllerUtil, likeStorage);
        filmController = new FilmController(filmService, filmValidator);

        mpa = Mpa.builder()
                .id(1)
                .name("G")
                .build();

        film = Film.builder()
                .name("Граф Монте-Кристо")
                .description("Исторический роман")
                .releaseDate(LocalDate.of(2002, 8, 1))
                .duration(120)
                .mpa(mpa)
                .build();
    }

    @AfterEach
    void clearFilmController() {
        if (filmStorage instanceof InMemoryFilmStorage) {
            filmController.getFilmsList().clear();
            try {
                Field field = InMemoryFilmStorage.class.getDeclaredField("filmId");
                field.setAccessible(true);
                field.set(field, 0);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    void durationShouldBeNotValid() {
        film.setDuration(-1);
        assertFalse(filmValidator.filmDurationValidator(film), "Фильм с продолжительностью меньше нуля - " +
                "не должен быть валиден.");
    }

    @Test
    void releaseDateShouldBeNotValid() {
        film.setReleaseDate(LocalDate.of(1895, Month.DECEMBER, 27));
        assertFalse(filmValidator.filmReleaseDateValidator(film), "Фильм с дата релиза " +
                "раньше 28 декабря 1895 года - не должен быть влиден");
    }

    @Test
    void shouldBeThrowsValidationExceptionBecauseDurationIsNegative() {
        film.setDuration(-1);
        ValidationException validationException = assertThrows(ValidationException.class,
                                                               () -> filmController.addFilm(film));
        assertTrue(validationException.getMessage()
                .contentEquals("Продолжительность фильма должна быть положительной !!!"));
    }

    @Test
    void shouldBeThrowsValidationExceptionBecauseReleaseDateIsWrong() {
        film.setReleaseDate(LocalDate.of(1895, Month.DECEMBER, 27));
        ValidationException validationException = assertThrows(ValidationException.class,
                                                               () -> filmController.addFilm(film));
        assertTrue(validationException.getMessage()
                .contentEquals("Дата релиза должна быть не раньше 28 декабря 1895 года !!!"));
    }
}