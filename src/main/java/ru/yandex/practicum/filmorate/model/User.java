package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@SuperBuilder
public class User {
    private int id;
    private String email;
    private String login;
    private String name;
    private String birthday;
    private final Set<Integer> friends = new HashSet<>();

    public void addNewFriend(Integer friendId) {
        friends.add(friendId);
    }

    public void deleteFriend(Integer friendId) {
        friends.remove(friendId);
    }

    public Set<Integer> getAllFriends() {
        return friends;
    }
}