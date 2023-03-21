package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.film_util.LikeStorage;

@Component
public class LikeDbStorage implements LikeStorage {
    JdbcTemplate jdbcTemplate;

    public LikeDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void putLike(Integer filmId, Integer userId) {
        jdbcTemplate.update("MERGE INTO LIKES KEY (FILM_ID, USER_ID) VALUES (?, ?)"
                , filmId, userId);
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        jdbcTemplate.update("DELETE FROM LIKES WHERE FILM_ID = ? AND USER_ID = ?"
                , filmId, userId);
    }
}
