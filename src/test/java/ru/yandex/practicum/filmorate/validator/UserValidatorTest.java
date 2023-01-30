package ru.yandex.practicum.filmorate.validator;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertThrows;

class UserValidatorTest {
    private static final int ID = 123;
    private static final String NAME = "MyName";
    private static final String EMAIL = "myname@ya.ru";
    private static final String LOGIN = "login";
    private static final String BIRTHDAY = "1999-01-01";
    private static final String BLANK_STR = "   ";

    @Test
    void validateUserDataIsValid() throws ValidateException {
        User user = new User(ID, EMAIL, LOGIN, NAME, BIRTHDAY);
        UserValidator.validate(user);
    }

    @Test
    void validateEmailIsEmptyIsNotValid() {
        User user = new User(ID, "", LOGIN, NAME, BIRTHDAY);

        assertThrows(ValidateException.class, () -> UserValidator.validate(user));
    }

    @Test
    void validateEmailIncorrectIsNotValid() {
        User user = new User(ID, "badEmail", LOGIN, NAME, BIRTHDAY);

        assertThrows(ValidateException.class, () -> UserValidator.validate(user));
    }

    @Test
    void validateEmailIsBlankIsNotValid() {
        User user = new User(ID, BLANK_STR, LOGIN, NAME, BIRTHDAY);

        assertThrows(ValidateException.class, () -> UserValidator.validate(user));
    }

    @Test
    void validateLoginIsEmptyIsNotValid() {
        User user = new User(ID, EMAIL, "", NAME, BIRTHDAY);

        assertThrows(ValidateException.class, () -> UserValidator.validate(user));
    }

    @Test
    void validateLoginIsBlankIsNotValid() {
        User user = new User(ID, EMAIL, BLANK_STR, NAME, BIRTHDAY);

        assertThrows(ValidateException.class, () -> UserValidator.validate(user));
    }

    @Test
    void validateBirthdayInTheFutureIsNotValid() {
        LocalDate now = LocalDate.now();
        User user = new User(ID, EMAIL, LOGIN, NAME, now.plusDays(1).format(DateTimeFormatter.ISO_DATE));

        assertThrows(ValidateException.class, () -> UserValidator.validate(user));
    }
}