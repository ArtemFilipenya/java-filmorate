package ru.yandex.practicum.filmorate.validator;

import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public class UserValidator {
    public static void validate(User user) throws ValidateException {
        if (user.getName() == null || user.getName().isBlank()) {
            throw new ValidateException("User create Fail email");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidateException("User create Fail email");
        }
        if (!user.getEmail().contains("@")) {
            throw new ValidateException("User create Fail email");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidateException("User create Fail login");
        }
        if (LocalDate.parse(user.getBirthday()).isAfter(LocalDate.now())) {
            throw new ValidateException("User create Fail birthday");
        }
    }
}