package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
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
        filmStorage.addNewFilm(film);
        log.info(String.format("Film with id=[%d] has been created.", film.getId()));
        return film;
    }

    public void deleteFilm(Film film) {
        filmStorage.deleteFilm(film);
    }

    public Film updateFilm(Film film) {
        filmValidator.validate(film);
        filmStorage.update(film);
        log.info(String.format("Film with id=[%d] has been updated.", film.getId()));
        return film;
    }

    public void addLike (int filmId, int userId) {
            Film film = filmStorage.getFilmById(filmId);

            film.addLike(userId);
            filmStorage.update(film);
            log.info(String.format("User with id=[%d] added like to film with id=[%d]", userId, filmId));
    }

    public Film deleteLike(int filmId, int userId) {
        Film film = filmStorage.getFilmById(filmId);
        boolean result = film.deleteLike(userId);

        if (!result) {
            throw new NotFoundException(String.format("User didn't like film with id=[%d].", filmId));
        }
        log.info(String.format("User with id=[%d] deleted like of the film with id=[%d]", userId, filmId));
        return filmStorage.update(film);
    }

    public List<Film> getTop(int count) {
        List<Film> films = filmStorage.getAllFilms();

        if (count <= 0) {
            throw new ValidateException("Count cannot be less 0.");
        }
        return films.stream()
                .sorted(Comparator.comparingInt(Film::countLikes).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}