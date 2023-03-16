package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Service
public class GenreService {
    private final GenreStorage genreService;

    @Autowired
    public GenreService(GenreStorage genreService) {
        this.genreService = genreService;
    }

    public Genre getGenre(Integer id) {
        return genreService.getGenre(id);
    }

    public List<Genre> getGenres() {
        return genreService.getGenresList();
    }
}
