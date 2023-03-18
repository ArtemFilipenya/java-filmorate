package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository("genreDbStorage")
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> getGenres() {
        String sqlQueryToGetGenres = "SELECT * FROM genre";
        return jdbcTemplate.query(sqlQueryToGetGenres, this::makeGenre);
    }

    @Override
    public Genre getGenre(Integer id) {
        Genre genre;
        String sqlQueryToGetGenreById = "SELECT * FROM genre WHERE genre_id = ?";
        try {
            genre = jdbcTemplate.queryForObject(sqlQueryToGetGenreById, this::makeGenre, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Cannot find the Genre");
        }
        return genre;
    }

    private Genre makeGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("genre_id"))
                .name(resultSet.getString("genre_name"))
                .build();
    }
}