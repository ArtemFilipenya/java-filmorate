package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.*;

@Data
@Builder
public class Film {
    private Integer id;
    @NotBlank(message = "Name cannot be empty")
    private String name;
    @Size(max = 200, message = "Max length 200")
    private String description;
    @PastOrPresent(message = "Bad release date")
    private LocalDate releaseDate;
    @Positive(message = "Bad duration")
    private Integer duration;
    @EqualsAndHashCode.Exclude
    private final Set<User> likes = new HashSet<>();
    @EqualsAndHashCode.Exclude
    private final Set<Genre> genres = new TreeSet<>();
    @EqualsAndHashCode.Exclude
    @NotNull
    private Mpa mpa;

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", name);
        values.put("description", description);
        values.put("release_date", releaseDate);
        values.put("duration", duration);
        values.put("mpa", mpa.getId());
        return values;
    }
}