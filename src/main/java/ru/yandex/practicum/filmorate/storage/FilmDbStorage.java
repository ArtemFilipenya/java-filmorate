package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Repository("filmDbStorage") // Без этого не работает
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void saveFilm(Film film) {
        addFilmId(film);
        String sqlQueryToAddFilm = "INSERT into genre_films (film_id, genre_id) VALUES (?, ?)";
        List<Integer> genreIds = new ArrayList<>();
        for (Genre genre : film.getGenres()) {
            genreIds.add(genre.getId());
        }

        jdbcTemplate.batchUpdate(sqlQueryToAddFilm, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, film.getId());
                ps.setInt(2, genreIds.get(i));
            }

            @Override
            public int getBatchSize() {
                return genreIds.size();
            }
        });
    }

    @Override
    public void checkFilmForUpdate(Film film) {
        String sqlQueryForUpdateFilm = "UPDATE film " +
                "SET name = ?, description = ?, release_date = ?, duration = ?, mpa = ? WHERE film_id = ?";
        int numberOfRowsUpdate = jdbcTemplate.update(sqlQueryForUpdateFilm, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());
        if (numberOfRowsUpdate == 0) {
            throw new NotFoundException("Not found the film");
        }
    }

    @Override
    public Film update (Film film) {
        String sqlQueryForDeleteById = "DELETE FROM genre_films WHERE film_id = ?";
        String sqlQueryToAddFilmIdAndGenreId = "INSERT INTO genre_films (film_id, genre_id) VALUES (?, ?)";

        if (film.getGenres().size() == 0) {
            jdbcTemplate.update(sqlQueryForDeleteById, film.getId());
        }
        if (film.getGenres() != null && film.getGenres().size() != 0) {
            jdbcTemplate.update(sqlQueryForDeleteById, film.getId());
            film.getGenres().forEach(genre -> jdbcTemplate.batchUpdate(sqlQueryToAddFilmIdAndGenreId,
                    new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setInt(1, film.getId());
                    ps.setInt(2, genre.getId());
                }

                @Override
                public int getBatchSize() {
                    return 1;
                }
            }));
        }
        return getFilm(film.getId());
    }

    public List<Film> getFilms() {
        String sqlQueryToGetAllFilms = "SELECT film.*, mpa.mpa_name FROM film JOIN mpa ON film.mpa = mpa.mpa_id";
        return jdbcTemplate.query(sqlQueryToGetAllFilms, this::makeFilm);
    }

    @Override
    public Film getFilm(Integer id) {
        String sqlQueryToGetFilmById = "SELECT film_id, name, description, release_date, duration, film.mpa, mpa.mpa_name " +
                "FROM film JOIN MPA ON film.mpa = mpa.mpa_id WHERE film.film_id = ?";
        Film film;
        try {
            film = jdbcTemplate.queryForObject(sqlQueryToGetFilmById, this::makeFilm, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("There is no movie with this id");
        }
        return film;
    }

    @Override
    public void addLike(Integer userId, Integer filmId) {
        String sqlQueryToLikeFilm = "MERGE INTO likes (person_id, film_id) VALUES (?, ?)";
        jdbcTemplate.batchUpdate(sqlQueryToLikeFilm, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, userId);
                ps.setInt(2, filmId);
            }

            @Override
            public int getBatchSize() {
                return 1;
            }
        });
    }

    @Override
    public void deleteLike(Integer userId, Integer filmId) {
        String sqlQueryToDeleteLikeFromFilm = "DELETE FROM likes where person_id = ? AND film_id = ?";
        jdbcTemplate.update(sqlQueryToDeleteLikeFromFilm, userId, filmId);
    }

    @Override
    public boolean containsFilm(Integer id) {
        String sqlQueryToFindFilmById = "SELECT f.*, mpa.mpa_name FROM FILM AS f JOIN mpa ON f.mpa = mpa.mpa_id " +
                "WHERE f.film_id = ?";
        try {
            // Как ты писал, чтоб исправил не работает
            jdbcTemplate.queryForObject(sqlQueryToFindFilmById, this::makeFilm, id);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    private Film makeFilm(ResultSet resultSet, int rowSum) throws SQLException {
        Film film = Film.builder()
                .id(resultSet.getInt("film_id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .mpa(new Mpa(resultSet.getInt("mpa"), resultSet.getString("mpa_name")))
                .build();
        String sqlQueryToGetAllUsersByFilmId = "SELECT person.* FROM likes JOIN person ON" +
                " likes.person_id=person.person_id WHERE likes.film_id=?";
        film.getLikes().addAll(jdbcTemplate.query(sqlQueryToGetAllUsersByFilmId, this::makeUser, film.getId()));
        film.getGenres().addAll(findGenresByFilmId(film.getId()));
        return film;
    }

    private void addFilmId(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("film")
                .usingGeneratedKeyColumns("film_id");
        film.setId(simpleJdbcInsert.executeAndReturnKey(film.toMap()).intValue());
    }

    private User makeUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("person_id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
    }

    private Set<Genre> findGenresByFilmId(Integer id) {
        String sqlQueryToFindGenresByFilmId = "SELECT g.genre_id, g.genre_name FROM film AS f JOIN genre_films AS gf" +
                " ON f.film_id=gf.film_id JOIN genre AS g ON gf.genre_id=g.genre_id WHERE f.film_id = ?";
        return new HashSet<>(jdbcTemplate.query(sqlQueryToFindGenresByFilmId, this::makeGenre, id));
    }

    private Genre makeGenre(ResultSet resultSet, int rowSum) throws SQLException {
        return new Genre(resultSet.getInt("genre_id"), resultSet.getString("genre_name"));
    }
}