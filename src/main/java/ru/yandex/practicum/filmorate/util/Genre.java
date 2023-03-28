package ru.yandex.practicum.filmorate.util;

import java.util.HashSet;
import java.util.Set;

public class Genre {
    private static Set<String> genreSet = new HashSet<>(Set.of("Комедия", "Драма", "Мультфильм", "Триллер",
                                                               "Документальный", "Боевик"));

    public static boolean isGenreExists(String genre) {
        return genreSet.contains(genre);
    }
}
