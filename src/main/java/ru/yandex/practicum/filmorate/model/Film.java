package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@SuperBuilder
public class Film {
    private int id;
    private String name;
    private String description;
    private String releaseDate;
    private int duration;
    private List<Genre> genres;
    private MPA mpa;
    private final List<Integer> likes = new ArrayList<>();

    public void addLike(int userId) {
        likes.add(userId);
    }

    public boolean deleteLike(Integer userId) {
        return likes.remove(userId);
    }

    public int countLikes() {
        return likes.size();
    }
}