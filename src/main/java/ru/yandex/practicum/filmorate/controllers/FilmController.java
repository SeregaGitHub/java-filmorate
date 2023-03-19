package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.validators.FilmValidator;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmService filmService;
    private final FilmValidator filmValidator;

    @Autowired
    public FilmController(FilmService filmService, FilmValidator filmValidator) {
        this.filmService = filmService;
        this.filmValidator = filmValidator;
    }

    @GetMapping
    public List<Film> getFilmsList() {
        return filmService.getFilmsList();
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable("id") Integer id) {
        if (filmService.getFilm(id) == null) {
            log.warn("Ошибка пользователя: Фильм с id-" + id + " не найден.");
            throw new FilmNotFoundException("Фильм с id-" + id + " не найден.");
        } else {
            return filmService.getFilm(id);
        }
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        filmValidator.filmValidator(film);
        filmService.addFilm(film);
        log.info("Фильм \"" + film.getName() + "\" добавлен.");
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        filmValidator.filmValidator(film);
        if (filmService.getFilm(film.getId()) == null) {
            log.warn("Ошибка пользователя: Фильм " + film.getName() + " не найден.");
            throw new FilmNotFoundException("Фильм " + film.getName() + " не найден.");
        } else {
            filmService.updateFilm(film);
            log.info("Фильм \"" + film.getName() + "\" обновлён.");
            return film;
        }
    }

    @DeleteMapping("/{id}")
    public String deleteFilm(@PathVariable("id") Integer id) {
        if (filmService.deleteFilm(id) == null) {
            log.warn("Ошибка пользователя: Фильм с id-" + id + " не найден.");
            throw new FilmNotFoundException("Фильм с id-" + id + " не найден.");
        } else {
            log.info("Фильм с id-" + id + " удалён.");
            return "Фильм с id-" + id + " удалён.";
        }
    }

    @PutMapping("/{id}/like/{userId}")
    public String putLike(@PathVariable("id") Integer id, @PathVariable("userId") Integer userId) {
        filmService.putLike(id, userId);
        log.info("Пользователь с id-" + userId + " поставил лайк фильму с id-" + id + ".");
        return "Пользователь с id-" + userId + " поставил лайк фильму с id-" + id + ".";
    }

    @GetMapping("/popular")
    @ResponseBody
    public List<Film> getBestFilms(@RequestParam(defaultValue = "10") Integer count) {
        if (count <= 0) {
            log.warn("Ошибка пользователя: Число " + count + " - некорректно. Оно должно быть положительное.");
            throw new IncorrectParameterException("Число " + count + " - некорректно. " +
                                                  "Оно должно быть положительное.");
        }
        return filmService.getBestFilms(count);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public String deleteLike(@PathVariable("id") Integer id, @PathVariable("userId") Integer userId) {
        log.info("Пользователь с id-" + userId + " удалил лайк у фильма с id-" + id + ".");
        filmService.deleteLike(id, userId);
        return "Пользователь с id-" + userId + " удалил лайк у фильма с id-" + id + ".";
    }
}