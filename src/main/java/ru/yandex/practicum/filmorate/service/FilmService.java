package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;

@Service
public class FilmService {
    private static int filmId = 0;
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public List<Film> getFilmsList() {
        return filmStorage.getFilmsList();
    }

    public Film addFilm(Film film) {
        film.setId(++filmId);
        filmStorage.addFilm(film);
        return film;
    }

    public Film updateFilm(Film film) {
        filmStorage.addFilm(film);
        return film;
    }

    public void putLike(Integer filmId, Integer userId) {
        filmStorage.putLike(filmId, userId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        filmStorage.deleteLike(filmId, userId);
    }

    public List<Film> getBestFilms(Integer count) {
        return filmStorage.getBestFilms(count);
    }

    public Film getFilm(Integer filmId) {
        return filmStorage.getFilm(filmId);
    }
}
