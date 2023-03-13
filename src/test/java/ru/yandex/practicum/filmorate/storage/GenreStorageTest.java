package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.film.DBFilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreStorageTest {
    private final GenreStorage genreStorage;
    private final DBFilmStorage filmStorage;

    @Test
    void getByIdValidIdValidGenre() {
        Optional<Genre> genreOptional = Optional.ofNullable(genreStorage.getById(1));
        assertThat(genreOptional)
                .isPresent()
                .hasValueSatisfying(genre ->
                        assertThat(genre).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    void getByIdNotValidIdEmpty() {
        assertThrows(NotFoundException.class, () -> genreStorage.getById(10));
    }

    @Test
    void getAllExecute6Items() {
        List<Genre> genres = genreStorage.getAll();
        assertThat(genres).hasSize(6);
    }

    @Test
    void getByFilmId_filmId_correctGenres() {
        Film film = Film.builder()
                .name("MyName")
                .description("my description")
                .duration(150)
                .releaseDate("1999-01-01")
                .mpa(MPA.builder().id(1).build())
                .build();
        int filmId = filmStorage.addNewFilm(film).getId();
        List<Genre> testGenres = genreStorage.getAll().subList(0,5);
        genreStorage.addAllToFilmId(filmId, testGenres);
        List<Genre> genres = genreStorage.getByFilmId(filmId);
        assertThat(genres).hasSize(5).containsAll(testGenres);
    }
}