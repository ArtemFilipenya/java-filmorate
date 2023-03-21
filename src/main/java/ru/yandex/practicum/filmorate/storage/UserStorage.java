package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

import java.util.List;
import java.util.Map;

public interface UserStorage {
    void saveUser(User user);

    void delete(User user);

    void update(User user);

    List<User> getUsers();

    void addFriend(Integer userId, Integer friendId);

    void deleteFriend(Integer userId, Integer friendId);

    List<User> getCommonFriends(Integer userId, Integer friendId);

    List<User> getFriends(Integer friendId);

    User getUser(Integer userId);

    boolean containsUser(Integer id);

    boolean containsUser(User user);
}