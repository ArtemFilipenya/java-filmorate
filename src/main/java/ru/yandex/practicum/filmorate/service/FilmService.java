package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final FilmValidator filmValidator;

    @Autowired
    public FilmService(FilmStorage filmStorage, FilmValidator filmValidator) {
        this.filmStorage = filmStorage;
        this.filmValidator = filmValidator;
    }

    public Film addFilm(Film film) {
        if (filmStorage.containsFilm(film.getId())) {
            throw new NotFoundException("This movie already exists.");
        }
        filmValidator.validate(film);
<<<<<<< HEAD
        filmStorage.add(film);
        log.info("film was {} saved", film);
=======
        films.add(film);
        log.info("film {} saved", film);
>>>>>>> 8a7ce8c (v5.0)
        return film;
    }

    public Film updateFilm(Film film) {
        filmValidator.validate(film);
        return filmStorage.update(film);
    }

    public List<Film> getFilms() {
        log.info("films count: " + filmStorage.getFilms().size());
        return filmStorage.getFilms();
    }

    public void addLike(Integer userId, Integer filmId) {
        if (!filmStorage.containsUser(userId)) {
            throw new NotFoundException("Unable to like from a user that doesn't exist");
        }
        if (!filmStorage.containsFilm(filmId)) {
            throw new NotFoundException("Can't like a movie that doesn't exist");
        }
        filmStorage.addLike(userId, filmId);
    }

    public void deleteLike(Integer userId, Integer filmId) {
        if (!filmStorage.containsUser(userId)) {
            throw new NotFoundException("Unable to remove a like from a user that doesn't exist");
        }
        if (!filmStorage.containsFilm(filmId)) {
            throw new NotFoundException("Unable to remove like from a movie that doesn't exist");
        }
        filmStorage.deleteLike(userId, filmId);
    }

    public List<Film> getSortedFilms(Integer count) {
        Comparator<Film> sortFilm = (f1, f2) -> {
            Integer filmLikes1 = f1.getLikes().size();
            Integer filmLikes2 = f2.getLikes().size();
            return -1 * filmLikes1.compareTo(filmLikes2);
        };
        return filmStorage.getFilms().stream()
                .sorted(sortFilm)
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film getFilm(Integer filmId) {
        return filmStorage.getFilm(filmId);
    }
}