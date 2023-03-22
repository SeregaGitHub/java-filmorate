package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;

@Component
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> getFilmsList() {
        String sql = "select " +
                "f.FILM_ID, FILM_NAME, FILM_DESCRIPTION, FILM_RELEASE_DATE, FILM_DURATION, m.RATING_ID, RATING_NAME " +
                "from FILM f, RATING m where f.RATING_ID = m.RATING_ID";
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
        if (!films.isEmpty()) {
            for (Film f: films) {
                f.setGenres(getAllFilmGenre(f.getId()));
            }
        }
        return films;
    }

    @Override
    public List<Film> getBestFilms(Integer count) {
        String sql = "SELECT FILM.FILM_ID, FILM_NAME, FILM_DESCRIPTION, FILM_RELEASE_DATE, FILM_DURATION, " +
                "FILM.RATING_ID, RATING.RATING_NAME, COUNT (LIKES.USER_ID) AS likes_quantity " +
                "FROM FILM LEFT JOIN LIKES USING (FILM_ID) JOIN RATING USING (RATING_ID) GROUP BY FILM.FILM_ID " +
                "ORDER BY likes_quantity DESC LIMIT ?";
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), count);
        if (!films.isEmpty()) {
            for (Film f: films) {
                f.setGenres(getAllFilmGenre(f.getId()));
            }
        }
        return films;
    }

    @Override
    public Film addFilm(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {PreparedStatement statement = con.prepareStatement(
                "insert into FILM (FILM_NAME" +
                        ", FILM_DESCRIPTION, FILM_RELEASE_DATE, FILM_DURATION, RATING_ID) values (?, ?, ?, ?, ?)"
                , new String[]{"FILM_ID"});
            statement.setString(1, film.getName());
            statement.setString(2, film.getDescription());
            statement.setDate(3, Date.valueOf(film.getReleaseDate()));
            statement.setInt(4, film.getDuration());
            statement.setInt(5, film.getMpa().getId()); return statement;}, keyHolder);

        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        if (film.getGenres() !=null) {
            updateFilmGenres(film.getId(), film.getGenres());
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        int filmId = film.getId();
        jdbcTemplate.update("update FILM set FILM_NAME = ?, FILM_DESCRIPTION = ?, FILM_RELEASE_DATE = ?" +
                ", FILM_DURATION = ?, RATING_ID = ? where FILM_ID = ?", film.getName(), film.getDescription()
                , film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), filmId);

        TreeSet<Genre> filmGenres = film.getGenres();
        if (filmGenres != null) {
            jdbcTemplate.update("DELETE FROM FILM_GENRE WHERE FILM_ID = ?", filmId);
            updateFilmGenres(filmId, filmGenres);
        }
        return film;
    }

    @Override
    public Film getFilm(Integer id) {
        String sql = "select " +
                "f.FILM_ID, FILM_NAME, FILM_DESCRIPTION, FILM_RELEASE_DATE, FILM_DURATION, m.RATING_ID, RATING_NAME " +
                "from FILM f, RATING m where FILM_ID = ? and f.RATING_ID = m.RATING_ID";
        List<Film> list = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), id);
        Film film = null;
        if (list.size() == 1) {
            film = list.stream().findFirst().get();
            film.setGenres(getAllFilmGenre(film.getId()));
        }
        return film;
    }

    @Override
    public Film deleteFilm(Integer id) {
        Film film = getFilm(id);
        jdbcTemplate.update("delete from FILM where FILM_ID = ?", id);
        return film;
    }

    private TreeSet<Genre> getAllFilmGenre(Integer id) {
        String sql = "select * from GENRE where GENRE_ID IN (select GENRE_ID from FILM_GENRE where FILM_ID = ?)";
        List<Genre> list = jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs), id);
        return new TreeSet<>(list);
    }

    static Film makeFilm(ResultSet rs) throws SQLException {
        Mpa mpa = Mpa.builder()
                .id(rs.getInt("RATING_ID"))
                .name(rs.getString("RATING_NAME"))
                .build();

        Film film = Film.builder()
                .id(rs.getInt("FILM_ID"))
                .name(Objects.requireNonNull(rs.getString("FILM_NAME")))
                .duration(rs.getInt("FILM_DURATION"))
                .description(Objects.requireNonNull(rs.getString("FILM_DESCRIPTION")))
                .releaseDate(Objects.requireNonNull(rs.getDate("FILM_RELEASE_DATE")).toLocalDate())
                .mpa(mpa)
                .build();
        return film;
    }

    static Genre makeGenre(ResultSet rs) throws SQLException {
        Genre genre = Genre.builder()
                .id(rs.getInt("GENRE_ID"))
                .name(rs.getString("GENRE_NAME"))
                .build();
        return genre;
    }

    private void updateFilmGenres(Integer filmId, TreeSet<Genre> genreTreeSet) {
        final List<Genre> genres = new ArrayList<>(genreTreeSet);
        jdbcTemplate.batchUpdate("MERGE INTO FILM_GENRE KEY (FILM_ID, GENRE_ID) VALUES (?, ?)"
                , new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setInt(1, filmId);
                        ps.setInt(2, genres.get(i).getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return genres.size();
                    }
                });
    }
}
