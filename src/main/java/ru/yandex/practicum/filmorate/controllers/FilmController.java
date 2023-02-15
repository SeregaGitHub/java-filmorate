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
        FilmValidator.filmValidator(film);
        film.setId(++filmId);
        filmHashMap.put(filmId, film);
        log.info("Фильм \"" + film.getName() + "\" добавлен.");
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (filmHashMap.containsKey(film.getId())) {
            FilmValidator.filmValidator(film);
            filmHashMap.put(film.getId(), film);
            log.info("Фильм \"" + film.getName() + "\" обновлён.");
        } else {
            log.info("Валидация фильма " + film.getName() + " не пройдена: " +
                    "обновление отсутствующего фильма - невозможно.");
            throw new ValidationException("Фильм " + film.getName() + " отсутствует в колекции.");
        }
        return film;
    }
}