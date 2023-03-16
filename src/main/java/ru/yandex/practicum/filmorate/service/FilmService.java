package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage films;
    private final FilmValidator filmValidator = new FilmValidator();

    @Autowired
    public FilmService(@Qualifier("FilmDbStorage") FilmStorage films) {
        this.films = films;
    }

    public Film addFilm(Film film) throws ResponseStatusException {
        filmValidator.validate(film);
        films.add(film);
        log.info("film {} saved", film);
        return film;
    }

    public Film updateFilm(Film film) throws ValidateException {
        filmValidator.validate(film);
        return films.update(film);
    }

    public List<Film> getFilms() {
        log.info("films count: " + films.getFilmsList().size());
        return films.getFilmsList();
    }

    public void addLike(Integer userId, Integer filmId) throws ValidateException {
        if (userId <=0 || filmId <= 0) {
            throw new ValidateException("id and friendId cannot be less 0");
        }
        films.addLike(userId, filmId);
    }

    public void deleteLike(Integer userId, Integer filmId) throws ResponseStatusException {
        if (userId <=0 || filmId <= 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "id and friendId cannot be less 0");
        }
        films.deleteLike(userId, filmId);
    }

    public List<Film> getSortedFilms(Integer count) throws ResponseStatusException {
        if (count <= 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "count cannot be less 0");
        }
        Comparator<Film> sortFilm = (f1, f2) -> {
            Integer filmLikes1 = f1.getLikes().size();
            Integer filmLikes2 = f2.getLikes().size();
            return -1 * filmLikes1.compareTo(filmLikes2);

        };
        return films.getFilmsList().stream().sorted(sortFilm).limit(count)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public Film getFilm(Integer filmId) throws ResponseStatusException {
        if (filmId <= 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "id and friendId cannot be less 0");
        }
        return films.getFilm(filmId);
    }
}