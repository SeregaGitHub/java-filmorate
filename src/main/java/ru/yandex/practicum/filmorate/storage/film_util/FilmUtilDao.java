package ru.yandex.practicum.filmorate.storage.film_util;

import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

public interface FilmUtilDao {
    Genre getGenre(String id);

    Collection<Genre> getAllGenres();

    Mpa getMpa(String id);

    Collection<Mpa> getAllMpa();
}
