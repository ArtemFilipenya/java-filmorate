package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage{
    private int currentId = 1;
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(int id) {
        User user = users.get(id);

        if (user == null) {
            throw new NotFoundException(String.format("User with id=[%d] not found.", id));
        }
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException(String.format("User with id=[%d] not found.", user.getId()));
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User addNewUser(User user) {
        user.setId(currentId);
        users.put(currentId, user);
        ++currentId;
        return user;
    }

    @Override
    public void deleteUser(User user) {
        if (!users.containsValue(user)) {
            throw new NotFoundException(String.format("User with id=[%d] not found.", user.getId()));
        }
        users.remove(user.getId());
    }
}