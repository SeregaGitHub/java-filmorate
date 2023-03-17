package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> getFilmsList();
    Film addFilm(Film film);
    Film updateFilm(Film film);
    Film getFilm(Integer id);
    Film deleteFilm(Integer id);
}
