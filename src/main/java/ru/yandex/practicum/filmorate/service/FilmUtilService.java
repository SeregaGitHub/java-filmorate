package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film_util.FilmUtilDao;

import java.util.Collection;
import java.util.Optional;

@Service
public class FilmUtilService {
    private final FilmUtilDao filmUtilDao;

    @Autowired
    public FilmUtilService(FilmUtilDao filmUtilDao) {
        this.filmUtilDao = filmUtilDao;
    }

    public Optional<Genre> getGenre(String id) {
        return filmUtilDao.getGenre(id);
    }

    public Collection<Genre> getAllGenres() {
        return filmUtilDao.getAllGenres();
    }

    public Optional<Mpa> getMpa(String id) {
        return filmUtilDao.getMpa(id);
    }

    public Collection<Mpa> getAllMpa() {
        return filmUtilDao.getAllMpa();
    }
}
