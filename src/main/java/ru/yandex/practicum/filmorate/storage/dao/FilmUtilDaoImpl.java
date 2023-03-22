package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film_util.FilmUtilDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component
@Slf4j
public class FilmUtilDaoImpl implements FilmUtilDao {
    private final JdbcTemplate jdbcTemplate;

    public FilmUtilDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Genre getGenre(String id) {
        String sql = "select * from GENRE where GENRE_ID = ?";
        Collection<Genre> genres = jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs), id);
        Genre genre = null;
        if (genres.size() == 1) {
            genre = genres.stream().findFirst().get();
        }
        return genre;
    }

    @Override
    public Collection<Genre> getAllGenres() {
        String sql = "select * from GENRE ORDER BY GENRE_ID";
        Collection<Genre> genres = jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs));
        return genres;
    }

    static Genre makeGenre(ResultSet rs) throws SQLException {
        Genre genre = Genre.builder()
                .id(rs.getInt("GENRE_ID"))
                .name(rs.getString("GENRE_NAME"))
                .build();
        return genre;
    }

    @Override
    public Mpa getMpa(String id) {
        String sql = "select * from RATING where RATING_ID = ?";
        Collection<Mpa> mpaCollection = jdbcTemplate.query(sql, (rs, rowNum) -> makeMpa(rs), id);
        Mpa mpa = null;
        if (mpaCollection.size() == 1) {
            mpa = mpaCollection.stream().findFirst().get();
        }
        return mpa;
    }

    @Override
    public Collection<Mpa> getAllMpa() {
        String sql = "select * from RATING ORDER BY RATING_ID";
        Collection<Mpa> mpaCollection = jdbcTemplate.query(sql, (rs, rowNum) -> makeMpa(rs));
        return mpaCollection;
    }

    static Mpa makeMpa(ResultSet rs) throws SQLException {
        Mpa mpa = Mpa.builder()
                .id(rs.getInt("RATING_ID"))
                .name(rs.getString("RATING_NAME"))
                .build();
        return mpa;
    }
}
