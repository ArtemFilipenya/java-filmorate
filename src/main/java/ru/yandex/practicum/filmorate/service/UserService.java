package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
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
        if (userStorage.containsUser(user)) {
            throw new NotFoundException("This user already exists");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        userValidator.validate(user);
        userStorage.saveUser(user);
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
        log.info("Users count: " + userStorage.getUsers().size());
        return userStorage.getUsers();
    }

    public void addFriend(Integer userId, Integer friendId) {
        if (!userStorage.containsUser(userId) || !userStorage.containsUser(friendId)) {
            throw new NotFoundException("Users not found.");
        }
        userStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        if (!userStorage.containsUser(userId) || !userStorage.containsUser(friendId)) {
            throw new NotFoundException("you cannot delete a non-existent user");
        }
        userStorage.deleteFriend(userId, friendId);
    }

    public List<User> getCommonFriends(Integer userId, Integer friendId) {
        if (!userStorage.containsUser(userId) || !userStorage.containsUser(friendId)) {
            throw new NotFoundException("Unable to get friends list of non-existent user");
        }
        return  userStorage.getCommonFriends(userId, friendId);
    }

    public List<User> getFriends(Integer friendId) {
        if (!userStorage.containsUser(friendId)) {
            throw new NotFoundException("Unable to get friends list of non-existent user");
        }
        return userStorage.getFriends(friendId);
    }

    public User getUser(Integer userId) {
        if (!userStorage.containsUser(userId)) {
            throw new NotFoundException("No such user exists");
        }
        return userStorage.getUser(userId);
    }
}