package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film_util.LikeStorage;
import ru.yandex.practicum.filmorate.util.ControllerUtil;

import java.util.List;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final ControllerUtil controllerUtil;
    private final LikeStorage likeStorage;

    @Autowired
    public FilmService(@Qualifier ("filmDbStorage") FilmStorage filmStorage, ControllerUtil controllerUtil
            , LikeStorage likeStorage) {
        this.filmStorage = filmStorage;
        this.controllerUtil = controllerUtil;
        this.likeStorage = likeStorage;
    }

    public List<Film> getFilmsList() {
        return filmStorage.getFilmsList();
    }

    public Film getFilm(Integer filmId) {
        return filmStorage.getFilm(filmId);
    }

    public Film addFilm(Film film) {
        filmStorage.addFilm(film);
        return film;
    }

    public Film updateFilm(Film film) {
        filmStorage.updateFilm(film);
        return film;
    }

    public Film deleteFilm(Integer id) {
        return filmStorage.deleteFilm(id);
    }

    public void putLike(Integer filmId, Integer userId) {
        controllerUtil.isFilmAndUserExists(filmStorage, filmId, userId);
        likeStorage.putLike(filmId, userId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        controllerUtil.isFilmAndUserExists(filmStorage, filmId, userId);
        likeStorage.deleteLike(filmId, userId);
    }

    public List<Film> getBestFilms(Integer count) {
        return filmStorage.getBestFilms(count);
    }
}
