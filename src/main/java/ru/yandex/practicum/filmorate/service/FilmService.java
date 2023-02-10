package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {
    private final static int TOP = 10;
    private final FilmStorage filmStorage;
    private final FilmValidator filmValidator;

    public List<Film> getAllFilms() {
        return new ArrayList<>(filmStorage.getAllFilms());
    }

    public Film getFilmById(int filmId) {
        return filmStorage.getFilmById(filmId);
    }

    public Film addNewFilm(Film film) {
        filmValidator.validate(film);
        return filmStorage.addNewFilm(film);
    }

    public void deleteFilm(Film film) {
        filmStorage.deleteFilm(film);
    }

    public Film updateFilm(Film film) {
        filmValidator.validate(film);

        return filmStorage.update(film);
    }

    public void addLike (int filmId, int userId) {
            Film film = filmStorage.getFilmById(filmId);

            film.addLike(userId);
            filmStorage.update(film);
            log.info(String.format("User with id=[%d] added like to film with id=[%d]", userId, filmId));
    }

    public void deleteLike(int filmId, int userId) {
        Film film = filmStorage.getFilmById(filmId);

        film.deleteLike(userId);
        filmStorage.update(film);
        log.info(String.format("User with id=[%d] deleted like of the film with id=[%d]", userId, filmId));
    }

    public List<Film> getTop(int count) {
        List<Film> films = filmStorage.getAllFilms();

        if (count <= 0) {
            count = TOP;
        }
        films.sort(Comparator.comparingInt(Film::countLikes).reversed());
        return films.stream().limit(count).collect(Collectors.toList());
    }
}