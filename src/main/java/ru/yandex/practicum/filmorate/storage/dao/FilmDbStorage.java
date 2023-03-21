package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
        SqlRowSet filmRowSet = jdbcTemplate.queryForRowSet(
                "SELECT FILM.FILM_ID, FILM_NAME, FILM_DESCRIPTION, FILM_RELEASE_DATE, FILM_DURATION, RATING_ID" +
                        ", COUNT (LIKES.USER_ID) AS likes_quantity " +
                        "FROM FILM " +
                        "LEFT JOIN LIKES USING (FILM_ID) " +
                        "GROUP BY FILM.FILM_ID " +
                        "ORDER BY likes_quantity DESC LIMIT ?", count);
        //   В твоём прошлом ревью было заменчание -
        //   "лучше сделать запрос, который будет сразу возвращать нужное количество фильмов в правильном порядке"

        //   COUNT (LIKES.USER_ID) - мне нужен для того, чтобы отсортировать фильмы в правильном порядке ещё в БД,
        // и вернуть из неё только необходимое количество лучших фильмов, которое задаётся в параметрах запроса.
        //   Если не делать JOIN на таблицу с лайками, то у меня не получается отсортировать фильмы в БД и вернуть
        // из неё только лучшие. Ведь в таблице FILM отсутствует поле с количеством лайков т.к. оно было бы вычисляемое.
        //   Мне казалось, что вычисляемое поле - это плохая практика.

        //   В pull request не понятно - какое замечание критичное, а какое нет.
        // Могу я оставить этот запрос ? Или необходимо добавить в таблицу FILM поле с количеством лайков
        // и обновлять его при добавлении и удалении лайка от пользователя ?
        List<Film> films = new ArrayList<>();

        while (filmRowSet.next()) {
            Film film = makeFilm(filmRowSet, filmRowSet.getInt("FILM_ID"));
            films.add(film);
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
                ", FILM_DURATION = ?, RATING_ID = ? where FILM_ID = ?", film.getName(), film.getDescription(), film.getReleaseDate()
                , film.getDuration(), film.getMpa().getId(), filmId);
        TreeSet<Genre> filmGenres = film.getGenres();
        if (filmGenres != null) {
            jdbcTemplate.update("DELETE FROM FILM_GENRE WHERE FILM_ID = ?", filmId);
            updateFilmGenres(filmId, filmGenres);
        }
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

    @Override
    public Film deleteFilm(Integer id) {
        Film film = getFilm(id);
        jdbcTemplate.update("delete from FILM where FILM_ID = ?", id);
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
