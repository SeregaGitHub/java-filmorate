package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.util.ControllerUtil;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final ControllerUtil controllerUtil;

    @Autowired
    public FilmService(@Qualifier ("filmDbStorage") FilmStorage filmStorage, ControllerUtil controllerUtil) {
        this.filmStorage = filmStorage;
        this.controllerUtil = controllerUtil;
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
        Film film = filmStorage.getFilm(filmId);
        film.getLikes().add(userId);
        filmStorage.updateFilm(film);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        controllerUtil.isFilmAndUserExists(filmStorage, filmId, userId);
        Film film = filmStorage.getFilm(filmId);
        boolean checkUserId = film.getLikes().remove(userId);
        if (!checkUserId) {
            throw new UserNotFoundException("Пользователь с id-" + userId + " не найден.");
        }
        filmStorage.updateFilm(film);
    }

    public List<Film> getBestFilms(Integer count) {
        List<Film> filmList = filmStorage.getFilmsList();
        if (count > filmList.size()) {
            count = filmList.size();
        }
        List<Film> bestFilmList = filmList.stream()
                .sorted((a, b) -> Integer.compare(b.getLikes().size(), a.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
        return bestFilmList;
    }
}
