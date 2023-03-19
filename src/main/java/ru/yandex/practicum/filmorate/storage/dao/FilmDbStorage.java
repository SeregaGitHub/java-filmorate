package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;

@Component
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> getFilmsList() {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet("select * from FILM ORDER BY FILM_ID");
        List<Film> films = new ArrayList<>();

        while (rowSet.next()) {
            Film film = makeFilm(rowSet, rowSet.getInt("FILM_ID"));
            films.add(film);
        }
        return films;
    }

    @Override
    public List<Film> getBestFilms(Integer count) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(
                "SELECT FILM.FILM_ID, FILM_NAME, FILM_DESCRIPTION, FILM_RELEASE_DATE, FILM_DURATION, RATING_ID" +
                        ", COUNT (LIKES.USER_ID) AS likes_quantity " +
                        "FROM FILM " +
                        "LEFT JOIN LIKES USING (FILM_ID) " +
                        "GROUP BY FILM.FILM_ID " +
                        "ORDER BY likes_quantity DESC LIMIT ?", count);

        List<Film> films = new ArrayList<>();

        while (rowSet.next()) {
            Film film = makeFilm(rowSet, rowSet.getInt("FILM_ID"));
            films.add(film);
        }
        return films;
    }

    @Override
    public Film addFilm(Film film) {
        TreeSet<Genre> filmGenres = film.getGenres();

        jdbcTemplate.update("insert into FILM (FILM_NAME" +
                ", FILM_DESCRIPTION, FILM_RELEASE_DATE, FILM_DURATION, RATING_ID) values (?, ?, ?, ?, ?)"
                , film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration()
                , film.getMpa().getId());

        String nextUserId = "select FILM_ID as NEXT_FILM_ID from FILM where FILM_NAME = ? and FILM_DESCRIPTION = ?";
        String ratingName = "select RATING_NAME from RATING where RATING_ID = ?";

    // Следующая ниже часть кода нужна только для того, чтобы присвоить айди фильмам, проверяемым в тестах Postman
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(nextUserId, film.getName(), film.getDescription());
        int filmId = 1;
        if (sqlRowSet.next()) {
            filmId = sqlRowSet.getInt("NEXT_FILM_ID");
        }
        film.setId(filmId);
    // Айди фильмам присваиваются до этого участка кода.

        SqlRowSet sqlRowSetName = jdbcTemplate.queryForRowSet(ratingName, film.getMpa().getId());
        if (sqlRowSetName.next()) {
            film.getMpa().setName(Objects.requireNonNull(sqlRowSetName.getString("RATING_NAME")));
        }

        if (filmGenres != null) {
            for (Genre i: filmGenres) {
                jdbcTemplate.update("insert into FILM_GENRE (FILM_ID, GENRE_ID) values (?, ?)"
                        , film.getId(), i.getId());
            }
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String ratingName = "select RATING_NAME from RATING where RATING_ID = ?";
        String genreName = "select GENRE_NAME from GENRE where GENRE_ID = ?";
        int filmId = film.getId();

        jdbcTemplate.update("update FILM set FILM_NAME = ?, FILM_DESCRIPTION = ?, FILM_RELEASE_DATE = ?" +
                ", FILM_DURATION = ?, RATING_ID = ? where FILM_ID = ?", film.getName(), film.getDescription(), film.getReleaseDate()
                , film.getDuration(), film.getMpa().getId(), filmId);

        SqlRowSet rowSetRatingName = jdbcTemplate.queryForRowSet(ratingName, film.getMpa().getId());
        if (rowSetRatingName.next()) {
            film.getMpa().setName(Objects.requireNonNull(rowSetRatingName.getString("RATING_NAME")));
        }

        TreeSet<Genre> filmGenres = film.getGenres();

    // В тестах Postman у жанров только айди. В коде ниже я присваиваю им ещё и имена.
        TreeSet<Genre> filmGenresWithNames = new TreeSet<>();

        if (filmGenres != null) {
            for (Genre g: filmGenres) {
                SqlRowSet sqlRowGenreName = jdbcTemplate.queryForRowSet(genreName, g.getId());
                if (sqlRowGenreName.next()) {
                    Genre genre = Genre.builder()
                            .id(g.getId())
                            .name(sqlRowGenreName.getString("GENRE_NAME"))
                            .build();
                    filmGenresWithNames.add(genre);
                }
            }
        }
    // Имена присваиваются доэтого участка кода.

        Set<Integer> filmLikes = film.getLikes();
        jdbcTemplate.update("DELETE FROM FILM_GENRE WHERE FILM_ID = ?", filmId);
        jdbcTemplate.update("DELETE FROM LIKES WHERE FILM_ID = ?", filmId);

        if (filmGenres != null) {
            for (Genre i: filmGenres) {
                jdbcTemplate.update("MERGE INTO FILM_GENRE KEY (FILM_ID, GENRE_ID) VALUES (?, ?)"
                        , filmId, i.getId());
            }
        }
        if (filmLikes != null) {
            for (Integer i: filmLikes) {
                jdbcTemplate.update("MERGE INTO LIKES KEY (FILM_ID, USER_ID) VALUES (?, ?)"
                        , filmId, i);
            }
        }
        film.setGenres(filmGenresWithNames);
        return film;
    }

    @Override
    public Film getFilm(Integer id) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet("select * from FILM where FILM_ID = ?", id);
        Film film = null;
        if (rowSet.next()) {
            film = makeFilm(rowSet, id);
        }
        return film;
    }

    private Set<Genre> getAllFilmGenre(Integer id) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet("select * from FILM_GENRE where FILM_ID = ? " +
                "order by GENRE_ID", id);
        Set<Genre> set = new HashSet<>();

        while (rowSet.next()) {
            Integer genreId = rowSet.getInt("GENRE_ID");
            SqlRowSet rowSetGenre = jdbcTemplate.queryForRowSet("select * from GENRE where GENRE_ID = ?", genreId);
            Genre genre = null;
            if (rowSetGenre.next()) {
                genre = Genre.builder()
                             .id(genreId)
                             .name(rowSetGenre.getString("GENRE_NAME"))
                             .build();
            }
            set.add(genre);
        }
        return set;
    }

    private Set<Integer> getAllFilmLikes(Integer id) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet("select * from LIKES where FILM_ID = ?", id);
        Set<Integer> set = new HashSet<>();

        while (rowSet.next()) {
            Integer userId = rowSet.getInt("USER_ID");
            set.add(userId);
        }
        return set;
    }

    private Film makeFilm(SqlRowSet rowSet, Integer id) {
        int ratingId = rowSet.getInt("RATING_ID");
        SqlRowSet rowSetMpa = jdbcTemplate.queryForRowSet("select * from RATING where RATING_ID = ?", ratingId);
        Mpa mpa = null;
        if (rowSetMpa.next()) {
            mpa = Mpa.builder()
                    .id(ratingId)
                    .name(rowSetMpa.getString("RATING_NAME"))
                    .build();
        }

        Film film = Film.builder()
                .id(rowSet.getInt("FILM_ID"))
                .name(Objects.requireNonNull(rowSet.getString("FILM_NAME")))
                .duration(rowSet.getInt("FILM_DURATION"))
                .description(Objects.requireNonNull(rowSet.getString("FILM_DESCRIPTION")))
                .releaseDate(Objects.requireNonNull(rowSet.getDate("FILM_RELEASE_DATE")).toLocalDate())
                .mpa(mpa)
                .genres(new TreeSet<>(getAllFilmGenre(id)))
                .likes(new HashSet<>(getAllFilmLikes(id)))
                .build();
        return film;
    }

    @Override
    public Film deleteFilm(Integer id) {
        Film film = getFilm(id);
        jdbcTemplate.update("delete from FILM where FILM_ID = ?", id);
        return film;
    }
}
