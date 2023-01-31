package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int currentId = 1;

    @GetMapping(value = "/users")
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @PostMapping(value = "/users")
    public User addNewUser(@RequestBody User user) throws ValidateException {
        UserValidator.validate(user);
        user.setId(currentId);
        users.put(currentId, user);
        currentId++;
        log.info("User with id={} created.", user.getId());
        return user;
    }

    @PutMapping(value = "/users")
    public User update(@RequestBody User user) throws ValidateException, NotFoundException {
        UserValidator.validate(user);
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.info("User with id={} updated.", user.getId());
        } else {
            throw new NotFoundException("User update unknown");
        }
        return user;
    }

    @ExceptionHandler(ValidateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleValidationException(
            ValidateException exception
    ) {
        log.error(exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleNotFoundException(
            NotFoundException exception
    ) {
        log.error(exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exception.getMessage());
    }
}
