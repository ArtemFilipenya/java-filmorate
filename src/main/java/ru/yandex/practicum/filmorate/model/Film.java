package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class Film {
    private int id;
    private String name;
    private String description;
    private String releaseDate;
    private int duration;
    private final List<Integer> likes = new ArrayList<>();

    public void addLike(int userId) {
        likes.add(userId);
    }

    public void deleteLike(int userId) {
        likes.remove(userId);
    }

    public int countLikes() {
        return likes.size();
    }
}