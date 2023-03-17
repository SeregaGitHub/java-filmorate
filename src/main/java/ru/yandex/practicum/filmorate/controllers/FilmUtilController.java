package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.IncorrectFilmUtilParameterException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmUtilService;

import java.util.Collection;

@RestController
@RequestMapping
@Slf4j
public class FilmUtilController {
    private final FilmUtilService filmUtilService;

    @Autowired
    public FilmUtilController(FilmUtilService filmUtilService) {
        this.filmUtilService = filmUtilService;
    }

    @GetMapping("/genres/{id}")
    public Genre getGenre(@PathVariable ("id") String id) {
        if (filmUtilService.getGenre(id).isEmpty()) {
            log.warn("Ошибка пользователя: Жанр с id-" + id + " не найден.");
            throw new IncorrectFilmUtilParameterException("Жанр с id-" + id + " не найден.");
        } else {
            return filmUtilService.getGenre(id).get();
        }
    }

    @GetMapping("/genres")
    public Collection<Genre> getAllGenres() {
        return filmUtilService.getAllGenres();
    }

    @GetMapping("/mpa/{id}")
    public Mpa getMpa(@PathVariable ("id") String id) {
        if (filmUtilService.getMpa(id).isEmpty()) {
            log.warn("Ошибка пользователя: Возрастной рейтинг с id-" + id + " не найден.");
            throw new IncorrectFilmUtilParameterException("Возрастной рейтинг с id-" + id + " не найден.");
        } else {
            return filmUtilService.getMpa(id).get();
        }
    }

    @GetMapping("/mpa")
    public Collection<Mpa> getAllMpa() {
        return filmUtilService.getAllMpa();
    }
}
