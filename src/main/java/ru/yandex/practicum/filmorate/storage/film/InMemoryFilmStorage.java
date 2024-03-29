package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validators.FilmValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Qualifier("inMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {
    private static int filmId = 0;
    private final Map<Integer, Film> filmHashMap = new HashMap<>();

    @Override
    public List<Film> getFilmsList() {
        return new ArrayList<>(filmHashMap.values());
    }

    @Override
    public List<Film> getBestFilms(Integer count) {
        List<Film> filmList = getFilmsList();
        if (count > filmList.size()) {
            count = filmList.size();
        }
        List<Film> bestFilmList = filmList.stream()
                //.sorted((a, b) -> Integer.compare(b.getLikes().size(), a.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
        return bestFilmList;
    }

    @Override
    public Film addFilm(Film film) {
        FilmValidator.filmInMemoryValidator(film);
        film.setId(++filmId);
        filmHashMap.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        FilmValidator.filmInMemoryValidator(film);
        filmHashMap.put(film.getId(), film);
        return film;
    }

    @Override
    public Film getFilm(Integer id) {
        return filmHashMap.get(id);
    }

    @Override
    public Film deleteFilm(Integer id) {
        return filmHashMap.remove(id);
    }
}
