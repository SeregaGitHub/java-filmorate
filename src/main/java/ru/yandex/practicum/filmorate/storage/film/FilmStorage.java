package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> getFilmsList();
    void addFilm(Film film);
    void putLike(Integer filmId, Integer userId);
    void deleteLike(Integer filmId, Integer userId);
    List<Film> getBestFilms(int count);
    Film getFilm(Integer id);
}
