package ru.yandex.practicum.filmorate.validator;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@Component
public class UserValidator {
    public void validate(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidateException("User create Fail email.");
        }
        if (!user.getEmail().contains("@")) {
            throw new ValidateException("User create Fail email. The mail must contain '@'.");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidateException("User create Fail login. The Login cannot be empty.");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidateException("User create Fail birthday.");
        }
    }
}