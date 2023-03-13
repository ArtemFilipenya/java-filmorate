package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.film.DBFilmStorage;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmStorageTest {
    private final DBFilmStorage filmStorage;

    @Test
    void getByIdValidIdValidFilm() {
        Film testFilm = Film.builder()
                .name("MyName")
                .description("my description")
                .duration(150)
                .releaseDate("1999-01-01")
                .mpa(MPA.builder().id(1).build())
                .build();
        int filmId = filmStorage.addNewFilm(testFilm).getId();

        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilmById(filmId));
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", filmId)
                );
    }

    @Test
    void getByIdNotValidIdEmpty() {
        assertThrows(NotFoundException.class, () -> filmStorage.getFilmById(100));
    }
}