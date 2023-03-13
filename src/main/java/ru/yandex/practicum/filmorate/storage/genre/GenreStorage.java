package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {
    List<Genre> getAll();

    Genre getById(int id) throws NotFoundException;

    List<Genre> getByFilmId(int filmId) throws NotFoundException;

    void addAllToFilmId(int filmId, List<Genre> genre);

    void deleteAllByFilmId(int filmId);
}