package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;
import java.util.stream.Collectors;

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

    public Film getFilm(Integer filmId) {
        return filmStorage.getFilm(filmId);
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

    public Film deleteFilm(Integer id) {
        return  filmStorage.deleteFilm(id);
    }

    public void putLike(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilm(filmId);
        film.getLikes().add(userId);
        filmStorage.addFilm(film);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilm(filmId);
        film.getLikes().remove(userId);
        filmStorage.addFilm(film);
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
