package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validators.FilmValidator;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private static int filmId = 0;

    private final HashMap<Integer, Film> filmHashMap = new HashMap<>();

    @GetMapping
    public List<Film> getFilmsList() {
        return new ArrayList<>(filmHashMap.values());
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        boolean filmDurationValidation = FilmValidator.filmDurationValidator(film);
        boolean filmReleaseDateValidation = FilmValidator.filmReleaseDateValidator(film);

        if (!filmDurationValidation) {
            throw new ValidationException("Продолжительность фильма должна быть положительной !!!");
        } else if (!filmReleaseDateValidation) {
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года !!!");
        } else {
            film.setId(++filmId);
            filmHashMap.put(filmId, film);
            log.info("Фильм \"" + film.getName() + "\" добавлен.");
        }
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (filmHashMap.containsKey(film.getId())) {
            boolean filmDurationValidation = FilmValidator.filmDurationValidator(film);
            boolean filmReleaseDateValidation = FilmValidator.filmReleaseDateValidator(film);

            if (!filmDurationValidation) {
                throw new ValidationException("Продолжительность фильма должна быть положительной !!!");
            } else if (!filmReleaseDateValidation) {
                throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года !!!");
            } else {
                filmHashMap.put(film.getId(), film);
                log.info("Фильм \"" + film.getName() + "\" обновлён.");
            }
        } else {
            log.info("Валидация фильма " + film.getName() + " не пройдена: " +
                    "обновление отсутствующего фильма - невозможно.");
            throw new ValidationException("Фильм " + film.getName() + " отсутствует в колекции.");
        }
        return film;
    }

    private static void setFilmId(int filmId) {
        FilmController.filmId = filmId;
    }
}