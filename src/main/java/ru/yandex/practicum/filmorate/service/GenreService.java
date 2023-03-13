package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreStorage genreStorage;

    public List<Genre> getAll() {
        return genreStorage.getAll();
    }

    public Genre getById(Integer id) throws NotFoundException {
        return genreStorage.getById(id);
    }

    public List<Genre> getByFilmId(Integer filmId) throws NotFoundException {
        return genreStorage.getByFilmId(filmId);
    }

    public void updateForFilm(Integer filmId, List<Genre> genres) {
        genreStorage.deleteAllByFilmId(filmId);
        genreStorage.addAllToFilmId(filmId, genres);
    }
}