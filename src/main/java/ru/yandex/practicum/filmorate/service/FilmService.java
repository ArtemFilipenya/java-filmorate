package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    public FilmService(@Qualifier("filmDbStorage") FilmStorage films) {
        this.films = films;
    }

    public Film addFilm(Film film) throws ResponseStatusException {
        filmValidator.validate(film);
        films.add(film);
        log.info("film was {} saved", film);
        return film;
    }

    public Film updateFilm(Film film) throws ValidateException {
        filmValidator.validate(film);
        return films.update(film);
    }

    public List<Film> getFilms() {
        log.info("films count: " + films.getFilms().size());
        return films.getFilms();
    }

    public void addLike(Integer userId, Integer filmId) throws ValidateException {
        films.addLike(userId, filmId);
    }

    public void deleteLike(Integer userId, Integer filmId) throws ResponseStatusException {
        films.deleteLike(userId, filmId);
    }

    public List<Film> getSortedFilms(Integer count) throws ResponseStatusException {
        Comparator<Film> sortFilm = (f1, f2) -> {
            Integer filmLikes1 = f1.getLikes().size();
            Integer filmLikes2 = f2.getLikes().size();
            return -1 * filmLikes1.compareTo(filmLikes2);

        };
        return films.getFilms().stream()
                .sorted(sortFilm)
                .limit(count)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public Film getFilm(Integer filmId) throws ResponseStatusException {
        return films.getFilm(filmId);
    }
}