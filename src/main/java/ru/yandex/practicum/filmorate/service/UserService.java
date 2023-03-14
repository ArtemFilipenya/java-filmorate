package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.List;

@Service
@Slf4j
public class UserService {
    private final UserStorage users;
    private final UserValidator userValidator = new UserValidator();

    @Autowired
    public UserService(@Qualifier("UserDbStorage") UserStorage users) {
        this.users = users;
    }

    public User addUser(User user) throws ResponseStatusException {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        userValidator.validate(user);
        users.add(user);
        log.info("Пользователь {} сохранен", user);
        return user;
    }

    public User updateUser(User user) throws ResponseStatusException {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        userValidator.validate(user);
        users.update(user);
        log.info("Пользователь {} сохранен", user);
        return user;
    }

    public List<User> getUsers() {
        log.info("Текущее кол-во пользователей: " + users.getUsersList().size());
        return users.getUsersList();
    }

    public void addFriend(Integer userId, Integer friendId) throws ValidateException {
        if (userId <=0 || friendId <= 0) {
            throw new ValidateException("id and friendId cannot be less 0");
        }
        users.addFriend(userId, friendId);
    }

    public void deleteFriend(Integer userId, Integer friendId) throws ValidateException {
        if (userId <=0 || friendId <= 0) {
            throw new ValidateException("id and friendId cannot be less 0");
        }
        if (userId.equals(friendId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You cant delete yourself");
        }
        users.deleteFriend(userId, friendId);
    }

    public List<User> getCommonFriends(Integer userId, Integer friendId) throws ValidateException {
        if (userId <=0 || friendId <= 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "id and friendId cannot be less 0");
        }
        if (userId.equals(friendId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Unable to request mutual friends of self");
        }
        return  users.getCommonFriends(userId, friendId);
    }

    public List<User> getFriends(Integer friendId) throws ValidateException {
        if (friendId <=0 ) {
            throw new ValidateException("id and friendId cannot be less 0");
        }
        return users.getFriends(friendId);
    }

    public User getUser(Integer userId) {
        if (userId <= 0) {
            throw new ValidateException("id and friendId cannot be less 0");
        }
        return users.getUser(userId);
    }
}