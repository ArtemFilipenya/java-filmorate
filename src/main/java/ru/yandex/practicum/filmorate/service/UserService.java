package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final UserValidator userValidator;

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(int id) {
        return userStorage.getUserById(id);
    }

    public User addNewUser(User user) {
        userValidator.validate(user);
        checkUserName(user);
        userStorage.addNewUser(user);
        log.info(String.format("User with id=[%d] has been created.", user.getId()));
        return user;
    }

    public void deleteUser(User user) {
        userStorage.deleteUser(user);
        log.info(String.format("User with id=[%d] has been deleted.", user.getId()));
    }

    public User updateUser(User user) {
        checkUserName(user);
        userValidator.validate(user);
        userStorage.updateUser(user);
        log.info(String.format("User with id=[%d] has been updated.", user.getId()));
        return user;
    }

    public void addNewFriend(int userId, int friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        user.addNewFriend(friendId);
        friend.addNewFriend(userId);
        log.info(String.format("User with id=[%d] added user with id=[%d] to friends.", userId, friendId));
    }

    public void deleteFriend(int userId, int friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        user.deleteFriend(friendId);
        friend.deleteFriend(userId);
        log.info(String.format("User with id=[%d] delete friend with id=[%d]", userId, friendId));
    }

    public List<User> getCommonFriends(int userId, int friendId) {
        List<Integer> commonFriendsId = new ArrayList<>(getAllFriendsId(userId));
        commonFriendsId.retainAll(getAllFriendsId(friendId));
        List<User> commonFriends = new ArrayList<>();

        for (Integer id: commonFriendsId) {
            commonFriends.add(userStorage.getUserById(id));
        }
        return commonFriends;
    }

    public List<User> getAllFriends(int userId) {
        User user = userStorage.getUserById(userId);
        List<User> friends = new ArrayList<>();

        for (Integer friend: user.getFriends()) {
            friends.add(userStorage.getUserById(friend));
        }
        return friends;
    }

    public List<Integer> getAllFriendsId(int userId) {
        User user = userStorage.getUserById(userId);

        return new ArrayList<>(user.getAllFriends());
    }

    private void checkUserName(User user) {
        if (user.getName() == null || user.getName().isBlank() || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }
}
