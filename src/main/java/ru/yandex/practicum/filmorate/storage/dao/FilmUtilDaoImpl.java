package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film_util.FilmUtilDao;

import java.util.*;

@Component
@Slf4j
public class FilmUtilDaoImpl implements FilmUtilDao {
    private final JdbcTemplate jdbcTemplate;

    public FilmUtilDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Genre> getGenre(String id) {
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("select * from GENRE where GENRE_ID = ?", id);
        if (genreRows.next()) {
            Genre genre = Genre.builder()
                               .id(genreRows.getInt("GENRE_ID"))
                               .name(Objects.requireNonNull(genreRows.getString("GENRE_NAME")))
                               .build();
            return Optional.of(genre);
        } else {
            log.warn("Жанр {} не найден", id);
            return Optional.empty();
        }
    }

    @Override
    public Collection<Genre> getAllGenres() {
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("select * from GENRE ORDER BY GENRE_ID");
        Collection<Genre> genres = new ArrayList<>();

        while (genreRows.next()) {
            Genre genre = Genre.builder()
                               .id(genreRows.getInt("GENRE_ID"))
                               .name(Objects.requireNonNull(genreRows.getString("GENRE_NAME")))
                               .build();
            genres.add(genre);
        }
        return genres;
    }

    @Override
    public Optional<Mpa> getMpa(String id) {
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("select * from RATING where RATING_ID = ?", id);
        if (mpaRows.next()) {
            Mpa mpa = Mpa.builder()
                         .id(mpaRows.getInt("RATING_ID"))
                         .name(Objects.requireNonNull(mpaRows.getString("RATING_NAME")))
                         .build();
            return Optional.of(mpa);
        } else {
            log.warn("Возрастной рейтинг {} не найден", id);
            return Optional.empty();
        }
    }

    @Override
    public Collection<Mpa> getAllMpa() {
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("select * from RATING ORDER BY RATING_ID");
        Collection<Mpa> mpaCollection = new ArrayList<>();

        while (mpaRows.next()) {
            Mpa mpa = Mpa.builder()
                    .id(mpaRows.getInt("RATING_ID"))
                    .name(Objects.requireNonNull(mpaRows.getString("RATING_NAME")))
                    .build();
            mpaCollection.add(mpa);
        }
        return mpaCollection;
    }
}
