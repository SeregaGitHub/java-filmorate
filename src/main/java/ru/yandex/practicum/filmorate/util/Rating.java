package ru.yandex.practicum.filmorate.util;

import java.util.HashSet;
import java.util.Set;

public class Rating {
    private static Set<String> ratingSet = new HashSet<>(Set.of("G", "PG", "PG-13", "R", "NC-17"));

    public static boolean isRatingExists(String rating) {
        return ratingSet.contains(rating);
    }
}
