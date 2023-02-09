package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> getAllUsers();
    User getUserById(int id);
    User updateUser(User user);
    User addNewUser(User user);
    void deleteUser(User user);
}
