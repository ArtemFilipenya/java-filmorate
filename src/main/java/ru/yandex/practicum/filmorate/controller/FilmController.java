package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class FilmController {
    private int currentId = 1;
    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping(value = "/films")
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @PostMapping(value = "/films")
    public Film addNewFilm(@RequestBody Film film) throws ValidateException {
        FilmValidator.validate(film);
        film.setId(currentId);
        films.put(currentId, film);
        currentId++;
        log.info("Film with id={} created.", film.getId());
        return film;
    }

    @PutMapping(value = "/films")
    public Film updateFilm(@RequestBody Film film) throws ValidateException, NotFoundException {
        FilmValidator.validate(film);
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Film with id={} updated.", film.getId());
        } else {
            throw new NotFoundException("Film with id[" + film.getId() + "] not found.");
        }
        return film;
    }
}