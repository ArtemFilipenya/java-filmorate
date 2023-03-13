package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> getAllFilms();
    Film getFilmById(int id);
    Film update(Film film);
    Film addNewFilm(Film film);
    void deleteFilm(Film film);
    void addLike(Integer id, Integer userId);

    void removeLike(Integer id, Integer userId);

    boolean hasLikeFromUser(Integer id, Integer userId);

    List<Film> getTop(Integer count);
}