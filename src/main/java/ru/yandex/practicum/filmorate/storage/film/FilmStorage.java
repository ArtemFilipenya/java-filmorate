package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> getAllFilms();
    Film getFilmById(int id);
    Film update(Film film);
    Film addNewFilm(Film film);
    void deleteFilm(Film Film);
}