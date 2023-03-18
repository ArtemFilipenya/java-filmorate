package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.List;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;
    private final UserValidator userValidator;

    @Autowired
    public UserService(UserStorage userStorage, UserValidator userValidator) {
        this.userStorage = userStorage;
        this.userValidator = userValidator;
    }

    public User addUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        userValidator.validate(user);
        userStorage.add(user);
        log.info("User {} saved", user);
        return user;
    }

    public User updateUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        userValidator.validate(user);
        userStorage.update(user);
        log.info("User {} saved", user);
        return user;
    }

    public List<User> getUsers() {
        log.info("Users count: " + userStorage.getUsersList().size());
        return userStorage.getUsersList();
    }

    public void addFriend(Integer userId, Integer friendId) {
        userStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        userStorage.deleteFriend(userId, friendId);
    }

    public List<User> getCommonFriends(Integer userId, Integer friendId) {
        return  userStorage.getCommonFriends(userId, friendId);
    }

    public List<User> getFriends(Integer friendId) {
        return userStorage.getFriends(friendId);
    }

    public User getUser(Integer userId) {
        return userStorage.getUser(userId);
    }
}