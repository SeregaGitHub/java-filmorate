package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

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
    public void putLike(Integer filmId, Integer userId) {
        filmHashMap.get(filmId).getLikes().add(userId);
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        filmHashMap.get(filmId).getLikes().remove(userId);
    }

    @Override
    public List<Film> getBestFilms(int count) {
        if (count > filmHashMap.size()) {
            count = filmHashMap.size();
        }
        List<Film> bestFilmList = new ArrayList<>(filmHashMap.values().stream().sorted(
                        Comparator.comparingInt(v -> v.getLikes().size()))
                                                                 .skip(filmHashMap.size() - count)
                                                                 .collect(Collectors.toList()));
        Collections.reverse(bestFilmList);
        return bestFilmList;
    }

    @Override
    public Film getFilm(Integer id) {
        return filmHashMap.get(id);
    }

}
