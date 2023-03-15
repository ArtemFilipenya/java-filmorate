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
<<<<<<< HEAD
        if (userStorage.containsUser(user)) {
            throw new NotFoundException("This user already exists");
        }
=======
>>>>>>> 8a7ce8c (v5.0)
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        userValidator.validate(user);
<<<<<<< HEAD
        userStorage.add(user);
=======
        users.add(user);
>>>>>>> 8a7ce8c (v5.0)
        log.info("User {} saved", user);
        return user;
    }

    public User updateUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        userValidator.validate(user);
<<<<<<< HEAD
        userStorage.update(user);
=======
        users.update(user);
>>>>>>> 8a7ce8c (v5.0)
        log.info("User {} saved", user);
        return user;
    }

    public List<User> getUsers() {
<<<<<<< HEAD
        log.info("Users count: " + userStorage.getUsers().size());
        return userStorage.getUsers();
    }

    public void addFriend(Integer userId, Integer friendId) {
        if (!userStorage.containsUser(userId) || !userStorage.containsUser(friendId)) {
            throw new NotFoundException("Users not found.");
=======
        log.info("User count: " + users.getUsersList().size());
        return users.getUsersList();
    }

    public void addFriend(Integer userId, Integer friendId) {
        if (userId <=0 || friendId <= 0) {
            throw new NotFoundException("id and friendId cannot be less 0");
>>>>>>> 8a7ce8c (v5.0)
        }
        userStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(Integer userId, Integer friendId) {
<<<<<<< HEAD
        if (!userStorage.containsUser(userId) || !userStorage.containsUser(friendId)) {
            throw new NotFoundException("you cannot delete a non-existent user");
=======
        if (userId <=0 || friendId <= 0) {
            throw new ValidateException("id and friendId cannot be less 0");
        }
        if (userId.equals(friendId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cant delete yourself");
>>>>>>> 8a7ce8c (v5.0)
        }
        userStorage.deleteFriend(userId, friendId);
    }

    public List<User> getCommonFriends(Integer userId, Integer friendId) {
<<<<<<< HEAD
        if (!userStorage.containsUser(userId) || !userStorage.containsUser(friendId)) {
            throw new NotFoundException("Unable to get friends list of non-existent user");
=======
        if (userId <=0 || friendId <= 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "id and friendId cannot be less 0");
        }
        if (userId.equals(friendId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to request mutual friends of self");
>>>>>>> 8a7ce8c (v5.0)
        }
        return  userStorage.getCommonFriends(userId, friendId);
    }

    public List<User> getFriends(Integer friendId) {
<<<<<<< HEAD
        if (!userStorage.containsUser(friendId)) {
            throw new NotFoundException("Unable to get friends list of non-existent user");
=======
        if (friendId <=0 ) {
            throw new ValidateException("id and friendId cannot be less 0");
>>>>>>> 8a7ce8c (v5.0)
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