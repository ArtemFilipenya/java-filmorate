package ru.yandex.practicum.filmorate.validator;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmValidatorTest {
    private static final int ID = 123;
    private static final String NAME = "name";
    private static final String DESCRIPTION = "my description";
    private static final String RELEASE_DATE = "1999-01-01";
    private static final int DURATION = 150;
    private static final String BLANK_STR = "   ";
    private final FilmValidator filmValidator = new FilmValidator();

    private static final LocalDate CINEMA_BIRTH = LocalDate.of(1895, 12, 28);

    @Test
    void validateShouldNotThrowExceptionIfDataIsValid() {
        Film film = new Film(ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION);
        filmValidator.validate(film);
    }

    @Test
    void validateNameIsEmptyIsNotValid() {
        Film film = new Film(ID, "", DESCRIPTION, RELEASE_DATE, DURATION);

        assertThrows(ValidateException.class, () -> filmValidator.validate(film));
    }

    @Test
    void validateNameIsBlankIsNotValid() {
        Film film = new Film(ID, BLANK_STR, DESCRIPTION, RELEASE_DATE, DURATION);

        assertThrows(ValidateException.class, () -> filmValidator.validate(film));
    }

    @Test
    void validateShouldThrowExceptionIfDescriptionIsLongerThan200IsNotValid() {
        String longDescription = "Профессор университета Паркер Уилсон находит на вокзале потерявшегося щенка, " +
                "отправленного из Японии в Америку. Так как за ним никто не является, Паркеру приходится оставить щенка " +
                "у себя. За то время, что собака живёт у профессора, между ними возникает крепкая дружба. " +
                "Паркер очень сильно привязывается к своему новому приятелю. Каждый день Хатико провожает хозяина до " +
                "вокзала, когда тот отправляется на работу, а вечером приходит к вокзалу, чтобы встретить его. " +
                "В один из дней профессор скоропостижно умирает на лекции в университете от сердечного приступа. " +
                "Не дождавшись хозяина, Хатико продолжает приходить на станцию, не пропуская ни дня.";
        Film film = new Film(ID, BLANK_STR, longDescription, RELEASE_DATE, DURATION);

        assertThrows(ValidateException.class, () -> filmValidator.validate(film));
    }

    @Test
    void validateReleaseDateIsTooEarlyIsNotValid() {
        String releaseDate = CINEMA_BIRTH.minusDays(1).format(DateTimeFormatter.ISO_DATE);
        Film film = new Film(ID, BLANK_STR, DESCRIPTION, releaseDate, DURATION);

        assertThrows(ValidateException.class, () -> filmValidator.validate(film));
    }

    @Test
    void validateDurationIsNegativeIsNotValid() {
        Film film = new Film(ID, BLANK_STR, DESCRIPTION, RELEASE_DATE, -100);

        assertThrows(ValidateException.class, () -> filmValidator.validate(film));
    }

    @Test
    void validateDurationIsZeroIsNotValid() {
        Film film = new Film(ID, BLANK_STR, DESCRIPTION, RELEASE_DATE, 0);

        assertThrows(ValidateException.class, () -> filmValidator.validate(film));
    }
}