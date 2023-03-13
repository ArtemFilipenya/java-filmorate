package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DBGenreStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public DBGenreStorage(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> getAll() {
        String sqlQuery =
                "SELECT g.id, " +
                        "g.name " +
                        "FROM genres AS g;";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeGenre(rs));
    }

    @Override
    public Genre getById(int id) {
        String sqlQuery =
                "SELECT g.id, " +
                        "g.name " +
                        "FROM genres AS g " +
                        "WHERE g.id = ?;";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeGenre(rs), id)
                .stream()
                .findAny()
                .orElseThrow(() -> new NotFoundException("Genre with id=" + id + " does not exist."));
    }

    @Override
    public List<Genre> getByFilmId(int filmId) {
        String sqlQuery =
                "SELECT g.id, " +
                        "g.name " +
                        "FROM films_genres AS fg " +
                        "JOIN genres AS g ON fg.genre_id = g.id " +
                        "WHERE fg.film_id = ?;";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeGenre(rs), filmId);
    }

    @Override
    public void addAllToFilmId(int filmId, List<Genre> genres) {
        List<Genre> genresDistinct = genres.stream().distinct().collect(Collectors.toList());
        jdbcTemplate.batchUpdate(
                "INSERT INTO films_genres (genre_id, film_id) VALUES (?, ?);",
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement statement, int i) throws SQLException {
                        statement.setInt(1, genresDistinct.get(i).getId());
                        statement.setInt(2, filmId);
                    }
                    public int getBatchSize() {
                        return genresDistinct.size();
                    }
                }
        );
    }

    @Override
    public void deleteAllByFilmId(int filmId) {
        String sqlQuery = "DELETE FROM films_genres WHERE film_id = ?;";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        return new Genre(id, name);
    }
}