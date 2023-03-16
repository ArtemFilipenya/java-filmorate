package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository("filmDbStorage")
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void add(Film film) throws ResponseStatusException {
        if (dbContainsFilm(film)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This movie already exists.");
        }
        Integer filmId = addFilmInfo(film);
        film.setId(filmId); // Да, имеет
        String sqlQuery = "INSERT into genre_films (film_id, genre_id) VALUES (?, ?)";

        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(sqlQuery, filmId, genre.getId());
        }
    }

    @Override
    public Film update (Film film) throws NotFoundException {
        String sqlQuery = "UPDATE film " +
                "SET name = ?, description = ?, release_date = ?, duration = ?, mpa = ? WHERE film_id = ?";
        if (jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate()
                , film.getDuration(), film.getMpa().getId(), film.getId()) == 0) {
            throw new NotFoundException("Not found the film");
        }
        if (film.getGenres().size() == 0) {
            String sqlQuery2 = "DELETE FROM genre_films WHERE film_id = ?";
            jdbcTemplate.update(sqlQuery2, film.getId());
        }
        if (film.getGenres() != null && film.getGenres().size() != 0) {
            String sqlQuery2 = "DELETE FROM genre_films WHERE film_id = ?";
            jdbcTemplate.update(sqlQuery2, film.getId());
            String sqlQuery3 = "INSERT INTO genre_films (film_id, genre_id) VALUES (?, ?)";
            film.getGenres().forEach(genre -> jdbcTemplate.update(sqlQuery3, film.getId(), genre.getId()));
        }
        Film film2 = getFilm(film.getId());
        return film2;
    }

    public List<Film> getFilms() {
        String sqlQuery = "SELECT film.*, mpa.mpa_name FROM film JOIN mpa ON film.mpa = mpa.mpa_id";
        return jdbcTemplate.query(sqlQuery, this::makeFilm);
    }


    @Override
    public Film getFilm(Integer id) throws ResponseStatusException{
        String sqlQuery = "SELECT film_id, name, description, release_date, duration, film.mpa, mpa.mpa_name " +
                "FROM film JOIN MPA ON film.mpa = mpa.mpa_id WHERE film.film_id = ?";
        Film film;
        try {
            film = jdbcTemplate.queryForObject(sqlQuery, this::makeFilm, id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "there is no movie with this id");
        }
        return film;
    }

    @Override
    public void addLike(Integer userId, Integer filmId) throws ResponseStatusException {
        if (!dbContainsUser(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to like from a user that doesn't exist");
        }
        if (!dbContainsFilm(filmId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Can't like a movie that doesn't exist");
        }
        String sqlQuery = "INSERT INTO likes (person_id, film_id) VALUES (?, ?)";
        try {
            jdbcTemplate.update(sqlQuery, userId, filmId);
        } catch (DuplicateKeyException e ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error requesting to add a like to a movie.");
        }
    }

    @Override
    public void deleteLike(Integer userId, Integer filmId) throws ResponseStatusException {
        if (!dbContainsUser(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to remove a like from a user that doesn't exist");
        }
        if (!dbContainsFilm(filmId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to remove like from a movie that doesn't exist");
        }
        String sqlQuery = "DELETE FROM likes where person_id = ? AND film_id = ?";
        if (jdbcTemplate.update(sqlQuery, userId, filmId) == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie has no like");
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
        String sqlQuery = "SELECT person.* FROM likes JOIN person ON likes.person_id=person.person_id WHERE likes.film_id=?";
        film.getLikes().addAll(jdbcTemplate.query(sqlQuery, this::makeUser, film.getId()));
        film.getGenres().addAll(findGenresByFilmId(film.getId()));
        return film;
    }

    private int addFilmInfo(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("film")
                .usingGeneratedKeyColumns("film_id");
        return simpleJdbcInsert.executeAndReturnKey(film.toMap()).intValue();
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
        String sqlQuery = "SELECT g.genre_id, g.genre_name FROM film AS f JOIN genre_films AS gf ON f.film_id=gf.film_id " +
                "JOIN genre AS g ON gf.genre_id=g.genre_id WHERE f.film_id = ?";
        return new HashSet<>(jdbcTemplate.query(sqlQuery, this::makeGenre, id));
    }

    private boolean dbContainsUser(Integer userId) {
        String sqlQuery = "SELECT * FROM person WHERE person_id = ?";
        try {
            jdbcTemplate.queryForObject(sqlQuery, this::makeUser, userId);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    private boolean dbContainsFilm(Integer filmId) {
        String sqlQuery = "SELECT f.*, mpa.mpa_name FROM FILM AS f JOIN mpa ON f.mpa = mpa.mpa_id " +
                "WHERE f.film_id = ?";
        try {
            jdbcTemplate.queryForObject(sqlQuery, this::makeFilm, filmId);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    private boolean dbContainsFilm(Film film) throws EmptyResultDataAccessException {
        String sqlQuery = "SELECT f.*, mpa.mpa_name FROM FILM AS f JOIN mpa ON f.mpa = mpa.mpa_id " +
                "WHERE f.name = ? AND  f.description = ? AND f.release_date = ? AND f.duration = ? AND f.mpa = ?";
        try {
            jdbcTemplate.queryForObject(sqlQuery, this::makeFilm, film.getName(), film.getDescription(),
                    film.getReleaseDate(), film.getDuration(), film.getMpa().getId());
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    private Genre makeGenre(ResultSet resultSet, int rowSum) throws SQLException {
        return new Genre(resultSet.getInt("genre_id"), resultSet.getString("genre_name"));
    }
}
