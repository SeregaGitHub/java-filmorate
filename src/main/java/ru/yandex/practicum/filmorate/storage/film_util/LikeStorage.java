package ru.yandex.practicum.filmorate.storage.film_util;

public interface LikeStorage {
    void putLike(Integer filmId, Integer userId);
    void deleteLike(Integer filmId, Integer userId);
}
