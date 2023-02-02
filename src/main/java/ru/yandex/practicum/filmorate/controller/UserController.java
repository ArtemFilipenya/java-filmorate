package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private final UserValidator userValidator = new UserValidator();
    private int currentId = 1;

    @GetMapping
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User addNewUser(@RequestBody User user) {
        userValidator.validate(user);
        checkUserName(user);
        user.setId(currentId);
        users.put(currentId, user);
        currentId++;
        log.info("User with id={} created.", user.getId());
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        userValidator.validate(user);
        checkUserName(user);
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("User with this id does not exist.");
        }
        users.put(user.getId(), user);
        log.info("User with id={} updated.", user.getId());
        return user;
    }

    private void checkUserName(User user) {
        if (user.getName() == null || user.getName().isBlank() || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }
}