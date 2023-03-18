package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FilmStorage {
    void add(Film film);

    Film update(Film film);

    List<Film> getFilms();

    void addLike(Integer userId, Integer filmId);

    void deleteLike(Integer userId, Integer filmId);

    Film getFilm (Integer id);

    boolean containsFilm(Integer id);

    boolean containsFilm(Film film);

    boolean containsUser(Integer id);
}