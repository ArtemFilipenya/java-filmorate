package ru.yandex.practicum.filmorate.validator;

import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public class UserValidator {
    public static void validate(User user) throws ValidateException {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidateException("Email can't be empty.");
        }
        if (!user.getEmail().contains("@")) {
            throw new ValidateException("Email must contain '@'.");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidateException("Login cannot be empty and contain spaces.");
        }
        if (LocalDate.parse(user.getBirthday()).isAfter(LocalDate.now())) {
            throw new ValidateException("Birthday can't be in the future.");
        }
    }
}