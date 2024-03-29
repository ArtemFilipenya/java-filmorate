package ru.yandex.practicum.filmorate.validator;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@Component
public class FilmValidator {
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);

    public void validate(Film film) {
        if (film.getName().isBlank() || film.getName() == null) {
            throw new ValidateException("The film cannot be empty.");
        }
        if (film.getDescription().length() > 200) {
            throw new ValidateException("Max length od description is 200 characters.");
        }
        if (film.getReleaseDate().isBefore(CINEMA_BIRTHDAY)) {
            throw new ValidateException("Release date cannot be earlier than December 28, 1895.");
        }
        if (film.getDuration() <= 0) {
            throw new ValidateException("Duration cannot be negative.");
        }
    }
}