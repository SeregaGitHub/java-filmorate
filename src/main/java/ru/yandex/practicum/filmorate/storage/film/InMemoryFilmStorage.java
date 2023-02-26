package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> filmHashMap = new HashMap<>();

    @Override
    public List<Film> getFilmsList() {
        return new ArrayList<>(filmHashMap.values());
    }

    @Override
    public void addFilm(Film film) {
        filmHashMap.put(film.getId(), film);
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
