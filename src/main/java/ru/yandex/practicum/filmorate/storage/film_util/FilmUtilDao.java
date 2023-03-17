package ru.yandex.practicum.filmorate.storage.film_util;

import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.Optional;

public interface FilmUtilDao {
    Optional<Genre> getGenre(String id);
    Collection<Genre> getAllGenres();
    Optional<Mpa> getMpa(String id);
    Collection<Mpa> getAllMpa();
}
